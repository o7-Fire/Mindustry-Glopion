import Atom.Reflect.Reflect;
import org.junit.jupiter.api.Test;

public class HeadlessGraphicTest {
    @Test
    void runGlopion() {
        System.setProperty("test", "1");
        Reflect.debug = true;
        Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
        //DesktopLauncher.main(new String[]{"-debug"});
    }
}
