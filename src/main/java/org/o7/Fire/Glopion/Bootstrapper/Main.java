package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper.*;

public class Main extends Mod {
    public static String flavor = Core.settings.getString("glopion-flavor", "Release-" + Version.buildString());
    public static String baseURL = Core.settings.getString("glopion-url", "https://raw.githubusercontent.com/o7-Fire/Mindustry-Glopion/main/");
    public static ClassLoader classLoader;
    public static Class<? extends Mod> unloaded = null;
    public static Mod loaded = null;
    public static boolean downloadThing = false;
    public static BootstrapperUI bootstrapper;
    public static Fi jar;
    public static Main main;
    public static String classpath = "org.o7.Fire.Glopion.";
    public static String info = "None";
    public static final ArrayList<Throwable> error = new ArrayList<>();
    

    
    public Main() {
        classpath = classpath + flavor.split("-")[0] + "Launcher";
        Log.infoTag("Mindustry-Version", Version.buildString());
        Log.infoTag("Mindustry-Version-Combined", Version.combined());
        Log.infoTag("Glopion-Bootstrapper", "Flavor: " + flavor);
        Log.infoTag("Glopion-Bootstrapper", "Classpath: " + classpath);
        try {
            load();
        }catch(Throwable t){
            handleException(t);
        }
        main = this;
        bootstrapper = new BootstrapperUI();
        if (unloaded != null){
            try {
                loaded = unloaded.getDeclaredConstructor().newInstance();
            }catch(Exception e){
                handleException(e);
            }
        }
    }
    
    public static void disable() {
        Mods.LoadedMod mod = Vars.mods.getMod(Main.class);
        if (mod != null){
            Core.settings.put("mod-" + mod.name + "-enabled", false);
            mod.state = Mods.ModState.disabled;
            runOnUI(() -> Vars.ui.showInfoFade("Disabling Bootstrapper"));
        }
    }
  
    private static void downloadLibrary0(Iterator<Map.Entry<String, File>> iterator){
        if(!iterator.hasNext()){
            if(!Vars.headless)
                Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
            return;
        }
        Map.Entry<String, File> s = iterator.next();
        if(s.getValue().exists()){
            Core.app.post(()->downloadLibrary0(iterator));
            return;
        }
        Seq<URL> seq = Seq.with(downloadList.get(s.getKey()));
        Log.info("Downloading: " + s.getKey());
        if(Vars.headless){
            BootstrapperUI.download(seq.random().toExternalForm(), new Fi(s.getValue()), () -> {
                Core.app.post(() -> downloadLibrary0(iterator));
            }, Throwable::printStackTrace);
        }else{
            BootstrapperUI.downloadGUI(seq.random().toExternalForm(), new Fi(s.getValue()), () -> {
                Core.app.post(() -> downloadLibrary0(iterator));
            });
        }
    }
    public static void downloadLibrary(){
        StringBuilder sb = new StringBuilder("Following library need to be downloaded\n[");
        for(Map.Entry<String, File> s : downloadFile.entrySet()) {
            if(s.getValue().exists())continue;
            sb.append(s.getKey()).append(", ");
        }
        sb.append("]");
        final Iterator<Map.Entry<String, File>> iterator = new HashMap<>(downloadFile).entrySet().iterator();
        if(Vars.headless){
            Core.app.post(() -> downloadLibrary0(iterator));
        }else{
            Main.runOnUI(() -> Vars.ui.showCustomConfirm("Downloading Library", sb.toString(), "Download", "No", () -> Core.app.post(() -> downloadLibrary0(iterator)), () -> {}));
        }
    }
    public static void load() {
        if (System.getProperty("glopion.loaded", "0").equals("1")){
            Log.errTag("Glopion-Bootstrapper", "Trying to load multiple times !!!");
            return;
        }
        System.setProperty("glopion.loaded", "1");
        
        String path = flavor.replace('-', '/') + ".jar";
        jar = Core.files.cache(path);
        SharedBootstrapper.parent = Core.files.cache("libs").file();
       
        if (jar.exists()){
            Log.infoTag("Glopion-Bootstrapper", "Loading: " + jar.absolutePath());
            try {
                ClassLoader parent = Main.class.getClassLoader();
                Log.info(parent.getClass().getSimpleName());
                classLoader = Vars.platform.loadJar(jar, parent);
                InputStream is = classLoader.getResourceAsStream("dependencies");
                if(is != null){
                    Log.info("found dependencies list");
                    if(Vars.mobile)
                        Log.err("IN MOBILE");
                    checkDependency(is);
                }
                
                if(!Vars.mobile && somethingMissing()){
                    downloadLibrary();
                }else{
                    if(downloadFile.size() != 0){
                        URL[] urls = new URL[downloadFile.values().size() + 1];
                        int i = 0;
                        for(File s : downloadFile.values())
                            urls[i++] = (s.toURI().toURL());
                        urls[i] = jar.file().toURI().toURL();
                        classLoader = new URLClassLoader(urls, parent);
                    }
                    unloaded = (Class<? extends Mod>) Class.forName(classpath, true, classLoader);
                }
                StringBuilder sb = new StringBuilder().append("Class: ").append(unloaded).append("\n");
                sb.append("Flavor: ").append(flavor).append("\n");
                sb.append("Classpath: ").append(jar.absolutePath()).append("\n");
                sb.append("Size: ").append(jar.length()).append(" bytes\n");
                sb.append("Classloader: ").append(classLoader.getClass()).append("\n");
                if(dependencies.size() != 0){
                    sb.append("Dependency: ").append("\n");
                    for(Object o : dependencies.keySet()){
                        sb.append(" -").append(o).append("=").append(downloadFile.get(String.valueOf(o))).append("\n");
                    }
                }
                info = sb.toString();
            }catch(Throwable e){
                handleException(e);
            }
        }else{
            Log.warn(jar.absolutePath() + " doesn't exist, loading in next startup to prevent game freeze");
            downloadThing = true;
        }
        
        
    }
   
    
    public static void handleException(Throwable e) {
        e.printStackTrace();
        error.add(e);
        Log.errTag("Glopion-Bootstrapper", e.toString());
        runOnUI(() -> Vars.ui.showException("Glopion-Bootstrapper Failed To Load", e));
    
    
    }
    
    public static void runOnUI(Runnable r) {
        if (Vars.ui != null && Vars.ui.loadfrag != null){
            r.run();
        }else{
            Events.on(EventType.ClientLoadEvent.class, cr -> runOnUI(r));
        }
    }
    
    @Override
    public void init() {
        bootstrapper.init();
        if (loaded != null) try {
            loaded.init();
        }catch(Exception e){
            handleException(e);
        }
    }
    
}
