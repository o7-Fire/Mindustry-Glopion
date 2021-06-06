package Premain;

import Atom.Bootstrap.AtomicBootstrap;
import Atom.Classloader.AtomClassLoader;
import Atom.Utility.Cache;

import java.net.URL;

public class Run {
    public static void main(String[] args) throws Throwable {
        AtomicBootstrap bootstrap = new AtomicBootstrap();
        bootstrap.atomClassLoader = new AtomClassLoader(new URL[]{}) {
        
        };
        bootstrap.loadCurrentClasspath();
        bootstrap.loadClasspath();
        if (System.getProperty("glopion-deepPatch") == null){
            //bootstrap.getLoader().addURL(Cache.tryCache("https://github.com/Anuken/Mindustry/releases/download/v126.2/Mindustry.jar"));
            System.setProperty("glopion-deepPatch", "1");
            System.setProperty("dev", "1");
        }
        bootstrap.loadMain("Premain.MindustryLauncher", args);
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
