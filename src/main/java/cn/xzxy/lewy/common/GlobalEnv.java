package cn.xzxy.lewy.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import rpc.domain.FileSplit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GlobalEnv {

    private static String dir;
    private static long scanningInterval;
    private static long blockSize;
    private static String serverIp;
    private static int sessionTimeOut;
    private static String jobTrackerPath;
    private static String engine1path;
    private static String engine2path;
    //30s扫描一次，将所有读取到的日志文件放入此队列供后面使用
    private static BlockingQueue<File> files =
            new LinkedBlockingQueue<>();
    //日志文件进行逻辑切块后，将切片对象存入此队列
    private static BlockingQueue<FileSplit> fileSplits =
            new LinkedBlockingQueue<>();

    static {
        initParam();
    }

    public static void initParam() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = GlobalEnv.class
                    .getResourceAsStream("/env.properties");
            properties.load(inputStream);
            inputStream.close();

            dir = properties.getProperty("tmobile.dir");
            scanningInterval = Long.parseLong(properties.getProperty("tmobile.scanningInterval"));
            blockSize = Long.parseLong(properties.getProperty("tmobile.blockSize"));
            serverIp = properties.getProperty("tmobile.zk.serverIp");
            sessionTimeOut = Integer.parseInt(properties.getProperty("tmobile.zk.sessionTimeOut"));
            jobTrackerPath = properties.getProperty("tmobile.zk.jobTrackerPath");
            engine1path = properties.getProperty("tmobile.zk.engine1path");
            engine2path = properties.getProperty("tmobile.zk.engine2path");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CuratorFramework zkConnect() {

        System.out.println("连接成功");

        return CuratorFrameworkFactory.builder()
                .connectString(serverIp).sessionTimeoutMs(sessionTimeOut)
                .connectionTimeoutMs(30000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    }

    public static String getDir() {
        return dir;
    }

    public static long getScanningInterval() {
        return scanningInterval;
    }

    public static long getBlockSize() {
        return blockSize;
    }

    public static String getServerIp() {
        return serverIp;
    }

    public static int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public static String getJobTrackerPath() {
        return jobTrackerPath;
    }

    public static String getEngine1path() {
        return engine1path;
    }

    public static String getEngine2path() {
        return engine2path;
    }

    public static BlockingQueue<File> getFiles() {
        return files;
    }

    public static BlockingQueue<FileSplit> getFileSplits() {
        return fileSplits;
    }
}
