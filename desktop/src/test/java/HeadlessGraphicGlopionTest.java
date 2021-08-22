import org.junit.jupiter.api.Test;
import org.o7.Fire.Glopion.Premain.Run;

public class HeadlessGraphicGlopionTest {
    @Test
    void desktopGlopion() throws Throwable {
        System.setProperty("test", "1");
        Run.main(new String[]{});
    }
}
