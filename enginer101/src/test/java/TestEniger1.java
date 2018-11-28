import cn.xzxy.lewy.common.OwnEnv;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestEniger1 {

    @Test
    public void testIp() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.println(hostAddress);
    }

    @Test
    public void testProp() {
        System.out.println(OwnEnv.getRpcport());
        System.out.println(OwnEnv.getZnodePath());
    }
}
