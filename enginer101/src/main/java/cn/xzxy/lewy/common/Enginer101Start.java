package cn.xzxy.lewy.common;

import cn.xzxy.lewy.mapper.MapperRunner;
import cn.xzxy.lewy.rpc.RpcClient;
import cn.xzxy.lewy.rpc.RpcServer;
import cn.xzxy.lewy.zookeeper.ZkConnectRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Enginer101Start {

    public static void main(String[] args) {
        System.out.println("一级引擎01节点启动");
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(new Thread(new ZkConnectRunner()));
        executorService.submit(new Thread(new RpcServer()));
        executorService.submit(new Thread(new MapperRunner()));
        executorService.submit(new Thread(new RpcClient()));
    }
}
