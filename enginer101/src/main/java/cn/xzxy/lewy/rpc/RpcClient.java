package cn.xzxy.lewy.rpc;

import cn.xzxy.lewy.common.GlobalEnv;
import cn.xzxy.lewy.common.OwnEnv;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.curator.framework.CuratorFramework;
import rpc.service.RpcHttpAppHost;

import java.net.InetSocketAddress;

/**
 * 用于一级和二级引擎相连的rpc客户端，发送mapper处理完成后的数据
 */
public class RpcClient implements Runnable{

    private CuratorFramework client;

    @Override
    public void run() {
        try {
            client = GlobalEnv.zkConnect();
            client.start();
            byte[] data = client.getData()
                    .forPath(GlobalEnv.getEngine2path());
            //System.out.println("将截取的路径为" + new String(data));
            //这边必须用new String(data)，而不能使用data.toString()
            System.out.println("引擎二节点数据：" + new String(data));//引擎二节点数据：192.168.80.1:8881
            String[] dataInfo = new String(data).split(":");
            String zkIpAddr = dataInfo[0];
            int zkPort = Integer.parseInt(dataInfo[1]);

            NettyTransceiver rpcClient = new NettyTransceiver(
                    new InetSocketAddress(zkIpAddr, zkPort));
            RpcHttpAppHost proxy = SpecificRequestor.getClient(
                    RpcHttpAppHost.class, rpcClient);

            while (true) {
                proxy.sendMap(OwnEnv.getMapQueue().take());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
