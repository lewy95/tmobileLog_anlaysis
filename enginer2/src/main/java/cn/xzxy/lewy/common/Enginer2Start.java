package cn.xzxy.lewy.common;

import cn.xzxy.lewy.rpc.RpcServer;
import cn.xzxy.lewy.zookeeper.ZkConnectRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Enginer2Start {

    public static void main(String[] args) {
        System.out.println("二级引擎启动");
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(new Thread(new ZkConnectRunner()));
        executorService.submit(new Thread(new RpcServer()));
    }
}
