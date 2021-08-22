package mindustry;

import mindustry.server.ServerLauncher;
import org.junit.jupiter.api.Test;

public class ServerHeadless {
    @Test
    void headless() {
        ServerLauncher.main(new String[]{});
    }
}
