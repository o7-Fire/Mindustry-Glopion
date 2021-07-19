import Atom.Reflect.UnThread;
import Atom.Utility.Pool;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class Untest {
    @Test
    void sanityCheck() {
        for (int i = 0; i < 1000; i++) {
            System.out.println(true == true);
        }
    }
    
    
    @Test
    void premainServerLauncher() throws IOException, NoSuchFieldException, IllegalAccessException {
        System.setProperty("test", "1");
        System.setProperty("dev", "1");
        Pool.daemon(() -> {
            UnThread.sleep(1000 * 60 * 5);
            System.exit(0);
        });
        org.o7.Fire.Glopion.Premain.ServerLauncher.main(new String[]{"host", "Ancient_Caldera", "sandbox"});
    }
    
    @Test
    void changeGlobal() {
        System.out.println("Flag setted");
        System.setProperty("Yeeeet", "1");
    }
}