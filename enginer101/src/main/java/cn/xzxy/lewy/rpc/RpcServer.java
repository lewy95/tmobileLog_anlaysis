package cn.xzxy.lewy.rpc;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;
import rpc.service.RpcFileSplit;

import java.net.InetSocketAddress;

public class RpcServer implements Runnable{

    @Override
    public void run() {
        NettyServer server =
                new NettyServer(new SpecificResponder(RpcFileSplit.class,
                        new RpcFileSplitImpl()),new InetSocketAddress(9991));

        System.out.println("引擎1的rpc服务器启动");
    }
}
