import org.junit.jupiter.api.Test;
import org.o7.Fire.Glopion.Bootstrapper.Java;
import org.o7.Fire.Glopion.Bootstrapper.StartServer;

public class AAAAAAAAA {
    @Test
    void sanityTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println("AAAAAAAA: " + i);
        }
    }
    
    @Test
    void changeGlobal() {
        System.setProperty("Yeeeet", "1");
    }
    
    @Test
    void checkGlobal() {
        assert System.getProperty("Yeeeet") == null;
    }
    
    @Test
    void bootstrapperJavaTest() throws Throwable {
        Java.test = true;
        Java.main(new String[]{"test"});
    }
    
    @Test
    void bootstrapperStartServerTest() throws Throwable {
        StartServer.test = true;
        StartServer.run();
    }
    
}
