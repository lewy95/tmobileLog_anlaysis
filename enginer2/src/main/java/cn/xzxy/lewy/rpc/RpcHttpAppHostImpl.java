package cn.xzxy.lewy.rpc;

import cn.xzxy.lewy.reducer.ReducerRunner;
import org.apache.avro.AvroRemoteException;
import rpc.domain.HttpAppHost;
import rpc.service.RpcHttpAppHost;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RpcHttpAppHostImpl implements RpcHttpAppHost {

    public static BlockingQueue<Map<CharSequence,HttpAppHost>> queue =
            new LinkedBlockingQueue<>();

    @Override
    public Void sendHttpAppHost(HttpAppHost httpAppHost) throws AvroRemoteException {
        return null;
    }

    @Override
    public Void sendMap(Map<CharSequence, HttpAppHost> httpAppHostMap) throws AvroRemoteException {
        System.out.println("二级引擎收到，当前map数：" + httpAppHostMap.size());
        queue.add(httpAppHostMap);
        //如果mapper的数量等于4，开启reducer线程
        //这里的4是写死的，应该从zk中获取
        if (queue.size() == 4) {
            new Thread(new ReducerRunner()).start();
            System.out.println("reducer开启");
        }
        return null;
    }
}
