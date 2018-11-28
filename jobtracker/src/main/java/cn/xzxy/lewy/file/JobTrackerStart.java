package cn.xzxy.lewy.file;

import cn.xzxy.lewy.zookeeper.ZkConnectRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobTrackerStart {

    public static void main(String[] args) {
        System.out.println("jobTracker启动");

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(new Thread(new FileHandle()));
        executorService.submit(new Thread(new FileToBlock()));
        executorService.submit(new Thread(new ZkConnectRunner()));

        //executorService.shutdown();
    }
}
