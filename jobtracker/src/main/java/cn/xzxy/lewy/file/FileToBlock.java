package cn.xzxy.lewy.file;

import cn.xzxy.lewy.common.GlobalEnv;
import rpc.domain.FileSplit;

import java.io.File;

/**
 * 线程类：从队列中取出日志文件并进行逻辑切块
 */
public class FileToBlock implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                //从队列中获取日志文件
                File file = GlobalEnv.getFiles().take();
                int num = (int) Math.ceil(file.length() *1.0 / GlobalEnv.getBlockSize());
                for (int i = 0; i < num; i++) {
                    FileSplit split = new FileSplit();
                    split.setPath(file.getPath());
                    split.setStart(i * GlobalEnv.getBlockSize());
                    if (i == num - 1) {
                        split.setLength(file.length() - split.getStart());
                    } else {
                        split.setLength(GlobalEnv.getBlockSize());
                    }

                    //查看切片
                    System.out.println(split);
                    //将Split加入到队列中
                    GlobalEnv.getFileSplits().add(split);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
