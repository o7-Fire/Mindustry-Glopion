package Premain;

import Atom.Bootstrap.AtomicBootstrap;

import java.io.File;
import java.io.FileNotFoundException;

public class Run {
    public static void main(String[] args) throws Throwable {
        
        AtomicBootstrap bootstrap = new AtomicBootstrap();
        File mindustry = new File("");
        if (!mindustry.exists()) throw new FileNotFoundException(mindustry.getAbsolutePath());
        bootstrap.loadCurrentClasspath();
        bootstrap.loadClasspath();
        bootstrap.getLoader().addURL(mindustry);
        bootstrap.loadMain("mindustry.desktop.DesktopLauncher", args);
    }
    
    public static class Server {
        public static void main(String[] args) throws Throwable {
            AtomicBootstrap bootstrap = new AtomicBootstrap();
            File mindustry = new File("cache/Anuken/Mindustry/releases/download/v126.2/Mindustry.jar");
            if (!mindustry.exists()) throw new FileNotFoundException(mindustry.getAbsolutePath());
            bootstrap.loadCurrentClasspath();
            bootstrap.loadClasspath();
            bootstrap.getLoader().addURL(mindustry);
            bootstrap.loadMain("mindustry.desktop.DesktopLauncher", args);
        }
    }
}
