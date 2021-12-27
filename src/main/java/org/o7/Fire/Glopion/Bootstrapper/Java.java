package org.o7.Fire.Glopion.Bootstrapper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Java {
    public static URL stableGlopion;
    
    public static boolean test;
    public static Properties config;
    
    static {
        try {
            loadConfig();
            stableGlopion = new URL("https://github.com/o7-Fire/Mindustry-Glopion/releases/download/" + config.getProperty(
                    "stableGlopion") + "/Mindustry-Glopion-DeepPatch.jar");
        }catch(MalformedURLException e){
            e.printStackTrace();
            System.out.println("impossible");
        }
    }

    public static void loadConfig() {
        if (config != null) return;
        config = new Properties();
        try {
            config.load(Java.class.getResourceAsStream("/glopion.bootstrapper.config.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadWithClassloader(String mainClass, String... classpath) throws Throwable {
        ArrayList<String> classPath = new ArrayList<>();
        classPath.addAll(Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator)));
        classPath.addAll(Arrays.asList(classpath));
        URL[] urls = new URL[classPath.size()];
        for (int i = 0; i < urls.length; i++) urls[i] = new File(classPath.get(i)).toURI().toURL();
        URLClassLoader cl = new URLClassLoader(urls);
        Class<?> c = cl.loadClass(mainClass);
        c.getMethod("main", String[].class).invoke(null, (Object) new String[]{});
    }

    public static Process load(String mainClass, String... classpath) throws IOException {
        ArrayList<String> classPath = new ArrayList<>();
        classPath.add(SharedBootstrapper.javaPath);
        classPath.add("-cp");
        classPath.add(String.join(File.pathSeparator, classpath) + File.pathSeparator +
                System.getProperty("java.class.path"));
        classPath.add(mainClass);
        return new ProcessBuilder(classPath).inheritIO().start();
    }

    public static File download(URL url) {
        File f = new File("cache", url.getFile().substring(1));
        if (f.exists()) return f;
        System.out.println("Downloading: " + url);
        try {
            SharedBootstrapper.download(url, f).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!f.exists()) throw new RuntimeException("Failed to download " + url);
        return f;
    }

    public static File downloadMindustry() {
        return download(SharedBootstrapper.getMindustryURL());
    }

    public static File getGlopion() {
        File f;
        f = new File("Mindustry-Glopion-Core.jar");
        if (f.exists()) return f;
        f = new File("Mindustry-Glopion-DeepPatch.jar");
        if (f.exists()) return f;
        f = new File("cache", "Mindustry-Glopion-DeepPatch.jar");
        if (f.exists()) return f;
        f = new File("cache", "Mindustry-Glopion-Core.jar");
        if (f.exists()) return f;
        f = new File("cache", stableGlopion.getFile().substring(1)).getAbsoluteFile();
        if (f.exists()) return f;
        return download(stableGlopion);
    }

    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            loadWithClassloader("mindustry.desktop.DesktopLauncher", downloadMindustry().getAbsolutePath(), getGlopion().getAbsolutePath());
            if (true) return;
            int exit = load("mindustry.desktop.DesktopLauncher", downloadMindustry().getAbsolutePath(), getGlopion().getAbsolutePath()).waitFor();
            System.exit(exit);
            return;
        }
        boolean headless = GraphicsEnvironment.isHeadless() && System.console() != null;
        System.out.println("Headless: " + headless);
        boolean training = false;
        if (args.length != 0) {
            if (Arrays.asList(args).contains("training")) {
                training = true;
                System.out.println("Training");
            }
        }
        StringBuilder classPath = new StringBuilder(System.getProperty("java.class.path"));
        System.out.println(classPath);
        File glopion = new File("cache", stableGlopion.getFile().substring(1)).getAbsoluteFile();
        File mindustry;
        ArrayList<Thread> downloading = new ArrayList<>();
        
        System.out.println("Downloading mindustry");
        mindustry = new File("cache", SharedBootstrapper.getMindustryURL().getFile().substring(1)).getAbsoluteFile();
        downloading.add(SharedBootstrapper.download(SharedBootstrapper.getMindustryURL(), mindustry));
        
        if (!glopion.exists()){
            System.out.println("Downloading: " + stableGlopion);
            downloading.add(SharedBootstrapper.download(stableGlopion, glopion));
        }
        
        SharedBootstrapper.waitForThreads(downloading);
        URLClassLoader resource = new URLClassLoader(new URL[]{glopion.toURI().toURL()});
        try {
            SharedBootstrapper.checkDependency(resource.getResourceAsStream("dependencies"));
        }catch(Exception e){
            e.printStackTrace();
        }
        SharedBootstrapper.downloadAll();
        classPath.append(File.pathSeparator).append(glopion.getAbsolutePath());
        classPath.append(File.pathSeparator).append(mindustry.getAbsolutePath());
        for (File f : SharedBootstrapper.getFiles())
            classPath.append(File.pathSeparator).append(f.getAbsolutePath());
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(SharedBootstrapper.javaPath, "-cp", classPath.toString(), "org.o7.Fire.Glopion.Premain.Headless"));
        list.addAll(Arrays.asList(args));
        //if (training){ StartServer.run(); }
        if (test) return;//TODO hook local glopion
        System.exit(new ProcessBuilder(list.toArray(new String[0])).inheritIO().start().waitFor());
    }
}
