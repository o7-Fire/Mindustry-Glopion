import Atom.Reflect.UnThread;
import org.junit.jupiter.api.Test;
import org.o7.Fire.Glopion.Premain.ServerLauncher;

import java.io.IOException;


public class Untest {
    @Test
    void sanityCheck() {
        for (int i = 0; i < 10; i++) {
            System.out.println(true == true);
        }
        assert "1".equals("1");
    }
    
    
    @Test
    void premainServerLauncher() throws IOException, NoSuchFieldException, IllegalAccessException {
        assert System.getProperty("test") == null : "Impure enviroment";
        assert ServerLauncher.exception.size() == 0 : "Exception not empty";
    
        Thread kek = new Thread(() -> {
            try {
                System.setProperty("test", "1");
                System.setProperty("dev", "1");
                ServerLauncher.main(new String[]{"host", "Ancient_Caldera", "sandbox"});
            }catch(Throwable t){
                if (t instanceof VirtualMachineError) throw (VirtualMachineError) t;
                ServerLauncher.exception.add(t);
            }
            if (ServerLauncher.application != null) while (ServerLauncher.application.alive()) UnThread.sleep(100);
        });
        kek.start();
        try {
            kek.join(1000 * 60 * 5);//5 minute ??
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Found: " + ServerLauncher.exception.size() + ", Exception");
        for (Throwable t : ServerLauncher.exception)
            t.printStackTrace();
    
        assert ServerLauncher.exception.size() == 0 : "Exception not empty";
    }
    
    @Test
    void changeGlobal() {
        assert System.getProperty("Yeeeet") == null : "Impure enviroment";
        System.out.println("Flag setted");
        System.setProperty("Yeeeet", "1");
    }
}
