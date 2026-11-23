package org.o7.Fire.Glopion.Bootstrapper;

import java.io.File;
import java.io.IOException;

public class StartServer {
    public static boolean test;
    public static void main(String[] args) throws IOException, InterruptedException {
        System.exit(run());
    }
    
    public static int run() throws IOException, InterruptedException {
        File server = SharedBootstrapper.getMindustryFile(SharedBootstrapper.MindustryType.Server);
        if (!server.exists())
            SharedBootstrapper.download(SharedBootstrapper.getMindustryURL(SharedBootstrapper.MindustryType.Server), server).join();
        if (test) return 0;
        return new ProcessBuilder(SharedBootstrapper.javaPath, "-jar", server.getAbsolutePath(), "host", "Ancient_Caldera", "sandbox").inheritIO().start().waitFor();
    }
}
