package org.o7.Fire.Glopion.Bootstrapper;

import java.io.File;
import java.io.IOException;

public class StartServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.exit(run().waitFor());
    }
    
    public static Process run() throws IOException, InterruptedException {
        File server = SharedBootstrapper.getMindustryFile(SharedBootstrapper.MindustryType.Server);
        if(!server.exists())
            SharedBootstrapper.download(SharedBootstrapper.getMindustryURL(SharedBootstrapper.MindustryType.Server), server).join();
        return new ProcessBuilder("java", "-jar", server.getAbsolutePath(), "host", "Ancient_Caldera", "sandbox").inheritIO().start();
    }
}
