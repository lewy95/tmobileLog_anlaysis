import cn.xzxy.lewy.common.GlobalEnv;
import org.junit.Test;

import java.io.File;

public class TestProp {

    @Test
    public void testJbProp(){
        System.out.println(GlobalEnv.getDir());
        System.out.println(GlobalEnv.getBlockSize());
    }

    @Test
    public void testFile(){
        File dir = new File(GlobalEnv.getDir());
        System.out.println(dir.getPath());
    }

}
