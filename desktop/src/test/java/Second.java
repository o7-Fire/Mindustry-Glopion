import org.junit.jupiter.api.Test;

import java.io.IOException;

public class Second {
    @Test
    void premainTest() throws IOException {
        org.o7.Fire.Glopion.Premain.Test.main(new String[]{});
    }
    
    @Test
    void checkGlobal() {
        assert System.getProperty("Yeeeet") == null : "Impure enviroment";
        System.out.println(System.getProperty("Yeeeet") == null);
    }
}
