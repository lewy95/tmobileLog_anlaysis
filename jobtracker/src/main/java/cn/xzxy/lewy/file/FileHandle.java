package cn.xzxy.lewy.file;

import cn.xzxy.lewy.common.GlobalEnv;

import java.io.File;

/**
 * 线程类FileHandle，每隔一段时间，扫描目录，
 * 如果发现标识文件，则证明此文件未读取，读取此文件，并将此文件放入队列中，同时删除标识文件
 * 定期扫描：将上面代码放入死循环，并sleep指定时间
 */
public class FileHandle implements Runnable {

    @Override
    public void run() {

        try {
            while (true) {
                //获取日志目录
                //获取目录下所有的文件
                File dir = new File(GlobalEnv.getDir());
                File[] files = dir.listFiles();
                for (File file : files) {
                    //对每一个文件进行判断，如果以.ctr结尾，则证明此文件处理
                    //最后处理完日志文件后，删除此标识文件
                    if (file.getName().endsWith(".ctr")) {
                        String csvName = file.getName().split("\\.")[0] + ".csv";
                        //获取需要处理的日志文件
                        File csvFile = new File(dir, csvName);
                        GlobalEnv.getFiles().add(csvFile);
                        //删除标识文件
                        file.delete();
                    }
                }
                //设置定期周期
                Thread.sleep(GlobalEnv.getScanningInterval());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
