package cn.xzxy.lewy.common;

import rpc.domain.FileSplit;
import rpc.domain.HttpAppHost;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OwnEnv {

    private static int rpcport;
    private static String znodePath;
    //用来存储jobtracker发送的切片对象
    private static BlockingQueue<FileSplit> splitQueue =
            new LinkedBlockingQueue<>();
    //处理完的数据需要放入map中，并将map存入此队列，map的数据类型<CharSequence,HttpAppHost>
    private static BlockingQueue<Map<CharSequence,HttpAppHost>> mapQueue =
            new LinkedBlockingQueue<>();

    static {
        initParam();
    }

    public static void initParam() {
        try {
            Properties properties = new Properties();
            InputStream inputStream =
                    OwnEnv.class.getResourceAsStream("/ownEnv.properties");
            properties.load(inputStream);
            inputStream.close();

            rpcport = Integer.parseInt(properties.getProperty("tmobile.rpcport"));
            znodePath = properties.getProperty("tombile.zk.znodePath");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getRpcport() {
        return rpcport;
    }

    public static String getZnodePath() {
        return znodePath;
    }

    public static BlockingQueue<FileSplit> getSplitQueue() {
        return splitQueue;
    }

    public static BlockingQueue<Map<CharSequence, HttpAppHost>> getMapQueue() {
        return mapQueue;
    }
}
