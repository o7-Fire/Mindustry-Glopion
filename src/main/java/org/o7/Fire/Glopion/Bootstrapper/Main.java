package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

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
    
    public static void load() {
        if (System.getProperty("glopion.loaded", "0").equals("1")){
            Log.errTag("Glopion-Bootstrapper", "Trying to load multiple times !!!");
            return;
        }
        System.setProperty("glopion.loaded", "1");
        
        String path = flavor.replace('-', '/') + ".jar";
        jar = Core.files.cache(path);
        if (jar.exists()){
            Log.infoTag("Glopion-Bootstrapper", "Loading: " + jar.absolutePath());
            try {
                ClassLoader parent = Main.class.getClassLoader();
                Log.info(parent.getClass().getSimpleName());
                classLoader = Vars.platform.loadJar(jar, parent);
                InputStream is = classLoader.getResourceAsStream("dependencies");
                if(is != null){
                    Log.info("found dependencies list");
                    checkDependency(is);
                }
                unloaded = (Class<? extends Mod>) Class.forName(classpath, true, classLoader);
                StringBuilder sb = new StringBuilder().append("Class: ").append(unloaded.getCanonicalName()).append("\n");
                sb.append("Flavor: ").append(flavor).append("\n");
                sb.append("Classpath: ").append(jar.absolutePath()).append("\n");
                sb.append("Size: ").append(jar.length()).append(" bytes\n");
                sb.append("Classloader: ").append(classLoader.getClass().getSimpleName());
                if(is != null){
                    sb.append("Dependency: ").append("\n");
                    for(Object o : dependencies.keySet()){
                        sb.append(" ").append(o).append("\n");
                    }
                }
                info = sb.toString();
            }catch(Throwable e){
                handleException(e);
                return;
            }
        }else{
            Log.warn(jar.absolutePath() + " doesn't exist, loading in next startup to prevent game freeze");
            downloadThing = true;
        }
        
        
    }
    public static Properties dependencies = new Properties();
    private static void checkDependency(InputStream is) throws IOException {
        dependencies.load(is);
        
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
