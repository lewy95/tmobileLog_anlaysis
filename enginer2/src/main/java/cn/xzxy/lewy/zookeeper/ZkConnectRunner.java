package cn.xzxy.lewy.zookeeper;

import cn.xzxy.lewy.common.GlobalEnv;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;

/**
 * 在zk中注册自己的节点，并将自己的ip和端口号注册到节点中
 */
public class ZkConnectRunner implements Runnable {

    private static CuratorFramework client;

    @Override
    public void run() {
        try {
            client = GlobalEnv.zkConnect();
            client.start();

            //获取本机的ip及端口号，端口号暂时写死了
            String data = InetAddress.getLocalHost().getHostAddress() +
                    ":" + "8881";
            //创建二级引擎节点
            client.create().withMode(CreateMode.EPHEMERAL)
                    .forPath(GlobalEnv.getEngine2path(),data.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
