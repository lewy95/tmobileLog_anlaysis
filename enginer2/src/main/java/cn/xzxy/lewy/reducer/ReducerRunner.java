package cn.xzxy.lewy.reducer;

import cn.xzxy.lewy.rpc.RpcHttpAppHostImpl;
import rpc.domain.HttpAppHost;

import java.util.HashMap;
import java.util.Map;

public class ReducerRunner implements Runnable {

    //结果集map
    private Map<String,HttpAppHost> map = new HashMap<>();

    @Override
    public void run() {

        while (true) {
            Map<CharSequence, HttpAppHost> ReducerMap =
                    RpcHttpAppHostImpl.queue.poll();
            if (ReducerMap == null) {
                //跳出循环
                System.out.println("队列数据reducer处理完毕");
                break;
            } else {
                for (Map.Entry<CharSequence, HttpAppHost> entry : ReducerMap.entrySet()) {
                    String key = entry.getKey().toString();
                    HttpAppHost hah = entry.getValue();
                    if (map.containsKey(key)) {
                        HttpAppHost maphah = map.get(key);
                        maphah.setAttemps(maphah.getAttemps() + hah.getAttemps());
                        maphah.setAccepts(maphah.getAccepts() + hah.getAccepts());
                        maphah.setTrafficUL(maphah.getTrafficUL() + hah.getTrafficUL());
                        maphah.setTracfficDL(maphah.getTracfficDL() + hah.getTracfficDL());
                        maphah.setRetranUL(maphah.getRetranUL() + hah.getRetranUL());
                        maphah.setRetranDL(maphah.getRetranDL() + hah.getRetranDL());
                        maphah.setFailCount(maphah.getFailCount() + hah.getFailCount());
                        maphah.setTransDelay(maphah.getTransDelay() + hah.getTransDelay());
                        map.put(key, maphah);
                    } else {
                        map.put(key, hah);
                    }
                }
            }
        }
        System.out.println("最后的map大小为："+map.size());
        System.out.println("mapreducer完成");
    }
}
