import org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper;

import java.io.File;
import java.io.IOException;

public class Download {
    public static void main(String[] args) throws IOException, InterruptedException {
        File desktop = SharedBootstrapper.getMindustryFile(SharedBootstrapper.MindustryType.Desktop);
        System.out.println(desktop.getAbsolutePath());
        //StartServer.run();
        if(!desktop.exists())
            SharedBootstrapper.download(SharedBootstrapper.getMindustryURL(SharedBootstrapper.MindustryType.Desktop), desktop).join();
        System.out.println("Done");
    }
}
