package org.o7.Fire.Glopion.Premain;

import Atom.Bootstrap.AtomicBootstrap;
import Atom.Utility.Cache;

import java.io.File;

public class Run {
    //run configuration
    //Classpath: Mindustry-Glopion.desktop.test
    //Class org.o7.Fire.Glopion.Premain.Run
    //JVM: 16
    public static void main(String[] args) throws Throwable {
        DIWHYClassloader diwhyClassloader = new DIWHYClassloader();
        for (String s : System.getProperty("java.class.path").split(File.pathSeparator))
            diwhyClassloader.addURL(new File(s).toURI().toURL());
      
        if (System.getProperty("glopion-deepPatch") == null){
            /*
            int h = 21254;
            URL u = new URL("https://github.com/Anuken/MindustryBuilds/releases/download/"+h+"/Mindustry-BE-Desktop-"+h+".jar");
            File mindustry = new File(new File(new File(FileUtility.getAppdata(), "Mindustry"), "build/cache/"), u.getFile());
            System.out.println(mindustry.getAbsolutePath() + ": " + mindustry.exists());
            diwhyClassloader.addURL(mindustry.toURI().toURL());
            
             */
            System.setProperty("glopion-deepPatch", "1");
            System.setProperty("dev", "1");
        }
      
        Class<?> main = Class.forName(Run.class.getPackageName()+".MindustryLauncher",true,diwhyClassloader);
        System.out.println(main.getClassLoader());
        main.getMethod("main", String[].class).invoke(null, (Object) args);
       
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
