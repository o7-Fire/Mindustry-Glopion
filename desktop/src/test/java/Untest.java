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
    void premainTest() throws IOException {
        org.o7.Fire.Glopion.Premain.Test.main(new String[]{});
    }
}
