package org.o7.Fire.Glopion.Premain;

import Atom.Bootstrap.AtomicBootstrap;
import Atom.Utility.Cache;

public class Run {
    //run configuration
    //Classpath: Mindustry-Glopion.desktop.test
    //Class org.o7.Fire.Glopion.Premain.Run
    //JVM: 16
    public static void main(String[] args) throws Throwable {
        if (System.getProperty("glopion-deepPatch") == null){
            System.setProperty("glopion-deepPatch", "1");
            System.setProperty("dev", "1");//let's assume you use Intellij Run Button
            System.out.println("DEV ?");
        }
        MindustryLauncher.main(args);
    }
    
    public static class Server {
        public static void main(String[] args) throws Throwable {
            AtomicBootstrap bootstrap = new AtomicBootstrap();
            bootstrap.loadCurrentClasspath();
            bootstrap.loadClasspath();
            bootstrap.getLoader().addURL(Cache.tryCache("https://github.com/Anuken/Mindustry/releases/download/v126.2/server-release.jar"));
            bootstrap.loadMain("mindustry.server.ServerLauncher", args);
        }
    }
}
