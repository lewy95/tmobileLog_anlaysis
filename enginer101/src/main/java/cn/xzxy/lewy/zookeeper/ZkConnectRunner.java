package cn.xzxy.lewy.zookeeper;

import cn.xzxy.lewy.common.GlobalEnv;
import cn.xzxy.lewy.common.OwnEnv;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 引擎一
 * 在zk中注册自己的节点，并将自己的ip和端口号注册到节点中
 */
public class ZkConnectRunner implements Runnable {

    private static CuratorFramework client;

    {
        client = GlobalEnv.zkConnect();
        client.start();
    }

    @Override
    public void run() {
        try {
            //获取本机的ip及端口号
            String data = InetAddress.getLocalHost().getHostAddress() +
                    ":" + OwnEnv.getRpcport();
            //判断是否存在一级引擎节点
            if (client.checkExists().forPath(GlobalEnv.getEngine1path()) == null) {
                client.create().forPath(GlobalEnv.getEngine1path());
            }

            client.create().withMode(CreateMode.EPHEMERAL).
                    forPath(GlobalEnv.getEngine1path() + OwnEnv.getZnodePath(),data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置繁忙状态
     */
    public static void setBusy() {
        try {
            String data = InetAddress.getLocalHost().getHostAddress()
                    + "/" + OwnEnv.getRpcport() + "/busy";
            //不确定版本号
            client.setData().forPath(GlobalEnv.getEngine1path()
                    + OwnEnv.getZnodePath(), data.getBytes());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置空闲状态
     */
    public static void setFree() {
        try {
            String data = InetAddress.getLocalHost().getHostAddress()
                    + "/" + OwnEnv.getRpcport() + "/free";
            //不确定版本号
            client.setData().forPath(GlobalEnv.getEngine1path()
                    + OwnEnv.getZnodePath(), data.getBytes());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
