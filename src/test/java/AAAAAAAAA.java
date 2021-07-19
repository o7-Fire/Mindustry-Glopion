import org.junit.jupiter.api.Test;
import org.o7.Fire.Glopion.Bootstrapper.Java;
import org.o7.Fire.Glopion.Bootstrapper.StartServer;

public class AAAAAAAAA {
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
