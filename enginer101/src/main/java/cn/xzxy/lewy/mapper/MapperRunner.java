package cn.xzxy.lewy.mapper;

import cn.xzxy.lewy.common.OwnEnv;
import cn.xzxy.lewy.zookeeper.ZkConnectRunner;
import rpc.domain.FileSplit;
import rpc.domain.HttpAppHost;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mapper线程类：
 * 1. 从队列中取出切片，然后进行位置追溯，按行对数据处理
 * 2. 按照业务逻辑对数据处理并封装对象
 */
public class MapperRunner implements Runnable {

    @Override
    public void run() {

        try {
            while (true) {
                Map<CharSequence, HttpAppHost> map = new HashMap<>();

                FileSplit fileSplit = OwnEnv.getSplitQueue().take();
                //设置一级引擎为繁忙状态
                ZkConnectRunner.setBusy();
                String path = fileSplit.getPath().toString();
                long start = fileSplit.getStart();
                long end = start + fileSplit.getLength();
                File file = new File(path);
                //为了保证每一行都是完整的，需要通过channel对文件进行处理
                FileChannel fileChannel = new FileInputStream(file).getChannel();
                //处理start
                if (start == 0) {
                } else {
                    long headPosition = start;
                    while (true) {
                        fileChannel.position(headPosition);
                        ByteBuffer buffer = ByteBuffer.allocate(1);
                        fileChannel.read(buffer);
                        if (new String(buffer.array()).equals("\n")) {
                            start = headPosition + 1;
                            break;
                        } else {
                            headPosition = headPosition - 1;
                        }
                    }
                }
                //处理end
                if (end == file.length()) {
                } else {
                    long lastPosition = end;
                    while (true) {
                        fileChannel.position(lastPosition);
                        ByteBuffer buffer = ByteBuffer.allocate(1);
                        fileChannel.read(buffer);
                        if (new String(buffer.array()).equals("\n")) {
                            end = lastPosition;
                            break;
                        } else {
                            lastPosition = lastPosition - 1;
                        }
                    }
                }
                System.out.println("start:" + start + ",last:" + end);
                //------------到此，start和end追溯完毕------------------

                //创建缓冲区存储切片数据
                System.out.println("mapper开启");
                ByteBuffer buffer = ByteBuffer.allocate((int) (end - start));
                fileChannel.position(start);
                fileChannel.read(buffer);
                //一行一行读取
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new ByteArrayInputStream(buffer.array())));
                //一行一行处理数据
                String line = null;

                while ((line = br.readLine()) != null) {
                    //创建对象
                    HttpAppHost hah = new HttpAppHost();
                    hah.setReportTime(path.split("_")[1]);
                    String[] tokens = line.split("\\|");
                    //设置其他属性
                    hah.setAppType(Integer.parseInt(tokens[22]));
                    hah.setAppSubType(tokens[23]);
                    hah.setUserIp(tokens[26]);
                    hah.setUserPort(Integer.parseInt(tokens[28]));
                    hah.setAppServerIp(tokens[30]);
                    hah.setAppServerPort(tokens[32]);
                    hah.setHost(tokens[58]);
                    //设置小区id，若不存在，则手动补9个0
                    if (tokens[16].equals("")) {
                        hah.setCellid("000000000");
                    } else {
                        hah.setCellid(tokens[16]);
                    }

                    int appTypeCode = Integer.parseInt(tokens[18]);
                    String transStatus = tokens[54];
                    String[] tmp = "10,11,12,13,14,15,32,33,34,35,36,37,38,48,49,50,51,52,53,54,55,199,200,201,202,203,204,205,206,302,304,306".split("\\,");
                    List<String> list = Arrays.asList(tmp);
                    String token67 = tokens[67];

                    if (appTypeCode == 103) {
                        hah.setAttemps(1);
                    } else {
                        hah.setAttemps(0);
                    }
                    if (appTypeCode == 103 && list.contains(transStatus) && token67.equals("")) {
                        hah.setAccepts(1);
                    } else {
                        hah.setAccepts(0);
                    }
                    if (appTypeCode == 103) {
                        hah.setTrafficUL(Long.parseLong(tokens[33]));
                        hah.setTracfficDL(Long.parseLong(tokens[34]));
                        hah.setRetranUL(Long.parseLong(tokens[39]));
                        hah.setRetranDL(Long.parseLong(tokens[40]));
                        hah.setTransDelay(Long.parseLong(tokens[20])
                                - Long.parseLong(tokens[19]));
                    }
                    int token54 = Integer.parseInt(tokens[54]);
                    if (appTypeCode == 103 && token54 == 1
                            && Integer.parseInt(token67) == 0) {
                        hah.setFailCount(1);
                    } else {
                        hah.setFailCount(0);
                    }
                    CharSequence hah_key = hah.getReportTime() + "|" + hah.getAppType() + "|"
                            + hah.getAppSubType() + "|" + hah.getUserIp() + "|"
                            + hah.getUserPort() + "|" + hah.getAppServerIp() + "|"
                            + hah.getAppServerPort() + "|" + hah.getHost() + "|"
                            + hah.getCellid();

                    //将对象存入map中，如果key已经存在，对value进行累加，否则存入
                    if (map.containsKey(hah_key)) {
                        HttpAppHost maphah = map.get(hah_key);
                        maphah.setAttemps(maphah.getAttemps() + hah.getAttemps());
                        maphah.setAccepts(maphah.getAccepts() + hah.getAccepts());
                        maphah.setTrafficUL(maphah.getTrafficUL() + hah.getTrafficUL());
                        maphah.setTracfficDL(maphah.getTracfficDL() + hah.getTracfficDL());
                        maphah.setRetranUL(maphah.getRetranUL() + hah.getRetranUL());
                        maphah.setRetranDL(maphah.getRetranDL() + hah.getRetranDL());
                        maphah.setFailCount(maphah.getFailCount() + hah.getFailCount());
                        maphah.setTransDelay(maphah.getTransDelay() + hah.getTransDelay());
                        map.put(hah_key, maphah);
                    } else {
                        map.put(hah_key, hah);
                    }
                }

                //将map存入队列中，最终发送给二级引擎
                OwnEnv.getMapQueue().add(map);
                //设置为空闲状态
                ZkConnectRunner.setFree();
                //测试，输出map的大小
                System.out.println("map的大小：" + map.size());
                System.out.println("mapper完成");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
