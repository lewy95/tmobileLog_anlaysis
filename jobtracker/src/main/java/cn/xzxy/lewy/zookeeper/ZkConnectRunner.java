package cn.xzxy.lewy.zookeeper;

import cn.xzxy.lewy.common.GlobalEnv;
import cn.xzxy.lewy.rpc.RpcClient;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * jobtracker
 * 连接zk获取一级引擎的名称并创建客户端
 */
public class ZkConnectRunner implements Runnable {

    private CuratorFramework client;

    @Override
    public void run() {
        try {
            client = GlobalEnv.zkConnect();
            client.start();

            //getChildren()只是获取子节点的名称
            List<String> childPaths = client.getChildren().forPath(GlobalEnv.getEngine1path());
            for (String path : childPaths) {
//                System.out.println(path);
                //有几个字节点就说明有几个以及引擎，就有几个rpc服务端
                //有几个服务端就需要创建多少个客户端
                new Thread(new RpcClient(path, client)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
