package Premain;

import Atom.Bootstrap.AtomicBootstrap;
import Atom.Utility.Cache;

public class Run {
    public static void main(String[] args) throws Throwable {
        AtomicBootstrap bootstrap = new AtomicBootstrap();
        bootstrap.loadCurrentClasspath();
        bootstrap.loadClasspath();
        if (System.getProperty("glopion-deepPatch") == null){
            bootstrap.getLoader().addURL(Cache.tryCache("https://github.com/Anuken/Mindustry/releases/download/v126.2/Mindustry.jar"));
            System.setProperty("glopion-deepPatch", "1");
        }
        bootstrap.loadMain("mindustry.desktop.DesktopLauncher", args);
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
