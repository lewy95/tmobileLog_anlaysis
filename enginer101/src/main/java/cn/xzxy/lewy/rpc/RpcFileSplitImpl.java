package cn.xzxy.lewy.rpc;

import cn.xzxy.lewy.common.OwnEnv;
import org.apache.avro.AvroRemoteException;
import rpc.domain.FileSplit;
import rpc.service.RpcFileSplit;

public class RpcFileSplitImpl implements RpcFileSplit{

    @Override
    public Void sendFileSplit(FileSplit fileSplit) throws AvroRemoteException {
        System.out.println("engine1收到的切片:" + fileSplit.toString());
        //将切片放入阻塞队列
        OwnEnv.getSplitQueue().add(fileSplit);
        return null;
    }
}
