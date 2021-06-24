package org.o7.Fire.Glopion.Bootstrapper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Java {
    public static URL stableGlopion;
    static {
        try {
            stableGlopion = new URL("https://github.com/o7-Fire/Mindustry-Glopion/releases/download/v4.3.2/Mindustry-Glopion-DeepPatch.jar");
        }catch(MalformedURLException e){
            e.printStackTrace();
            System.out.println("impossible");
        }
    }
    public static void main(String[] args)throws Throwable {
   
        boolean headless = GraphicsEnvironment.isHeadless() && System.console() != null;
        System.out.println("Headless: " + headless);
        if(!headless){
            JOptionPane.showMessageDialog(null, "use console", "Note", JOptionPane.INFORMATION_MESSAGE);
        }
        boolean training = false;
        if(args.length != 0) {
            if (Arrays.asList(args).contains("training")){
                training = true;
                System.out.println("Training");
            }else {
                System.out.println("Doing nothing ok");
                return;
            }
        }
        StringBuilder classPath = new StringBuilder(System.getProperty("java.class.path"));
        System.out.println(classPath);
        File glopion = new File("cache",stableGlopion.getFile().substring(1)).getAbsoluteFile();
        File mindustry;
        if(args.length != 0)
            mindustry = new File(args[0]).getAbsoluteFile();
        else {
            System.out.println("Downloading mindustry");
            mindustry = new File("cache", SharedBootstrapper.getMindustryURL().getFile().substring(1)).getAbsoluteFile();
            SharedBootstrapper.download(SharedBootstrapper.getMindustryURL(), mindustry);
        }
        if(!glopion.exists()){
            System.out.println("Downloading: " + stableGlopion);
            SharedBootstrapper.download(stableGlopion, glopion);
        }
        URLClassLoader resource = new URLClassLoader(new URL[]{glopion.toURI().toURL()});
        try {
            SharedBootstrapper.checkDependency(resource.getResourceAsStream("dependencies"));
        }catch(Exception e){
            e.printStackTrace();
        }
        SharedBootstrapper.downloadAll();
        classPath.append(File.pathSeparator).append(glopion.getAbsolutePath());
        classPath.append(File.pathSeparator).append(mindustry.getAbsolutePath());
        for(File f : SharedBootstrapper.getFiles())
            classPath.append(File.pathSeparator).append(f.getAbsolutePath());
        List<String> list = new ArrayList<>();
        File javaBin = new File(System.getProperty("java.home")+"/bin/java");
        String java = "java";
        if(javaBin.exists())
            java = javaBin.getAbsolutePath();
        list.addAll(Arrays.asList(java, "-cp", classPath.toString(), "org.o7.Fire.Glopion.Premain.Headless"));
        list.addAll(Arrays.asList(args));
        if(training){
            StartServer.run();
            
        }
        System.exit(new ProcessBuilder(list.toArray(new String[0])).inheritIO().start().waitFor());
    }
    
    
}
