package cn.xzxy.lewy.rpc;

import cn.xzxy.lewy.common.GlobalEnv;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import rpc.domain.FileSplit;
import rpc.service.RpcFileSplit;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * 用于jobtracker和一级引擎相连的rpc客户端
 */
public class RpcClient implements Runnable {

    private String path;
    private CuratorFramework client;

    public RpcClient(String path, CuratorFramework client) {
        this.path = path;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            byte[] data = client.getData()
                    .forPath(GlobalEnv.getEngine1path() + "/" + path);
            //System.out.println("将截取的路径为" + new String(data));
            //这边必须用new String(data)，而不能使用data.toString()
            System.out.println("引擎一节点数据：" + new String(data));//引擎一节点数据：192.168.80.1:9991
            String[] dataInfo = new String(data).split(":");
            String zkIpAddr = dataInfo[0];
            int zkPort = Integer.parseInt(dataInfo[1]);

            NettyTransceiver rpcClient = new NettyTransceiver(
                    new InetSocketAddress(zkIpAddr, zkPort));
            RpcFileSplit proxy = SpecificRequestor.getClient(
                    RpcFileSplit.class, rpcClient);

            //从队列中获取切片发送
            proxy.sendFileSplit(GlobalEnv.getFileSplits().take());

            while (true) {
                //CountDownLatch cdl = new CountDownLatch(1);
                //监听引擎1的数据，如果变化了，则获取其繁忙状态
                //如果空闲，则再发送一个切片，否则什么也不做
                NodeCache nodeCache = new NodeCache(client, GlobalEnv.getEngine1path()
                        + "/" + path, false);
                nodeCache.getListenable().addListener(new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        byte[] newData = nodeCache.getCurrentData().getData();
                        String state = new String(newData);
                        System.out.println("节点发生变化：" + state);
                        if (state.contains("free")) {
                            proxy.sendFileSplit(GlobalEnv.getFileSplits().take());
                        }
                        //cdl.countDown();
                    }
                });
                nodeCache.start(true);
                //cdl.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
