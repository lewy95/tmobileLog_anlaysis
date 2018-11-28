package cn.xzxy.lewy.rpc;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import rpc.service.RpcFileSplit;
import rpc.service.RpcHttpAppHost;

import java.net.InetSocketAddress;

public class RpcServer implements Runnable {

    @Override
    public void run() {
        NettyServer server =
                new NettyServer(new SpecificResponder(RpcHttpAppHost.class,
                        new RpcHttpAppHostImpl()),new InetSocketAddress(8881));

        System.out.println("二级引擎rpc服务器启动");
    }
}
