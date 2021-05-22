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

public class Main extends Mod {
    public static String flavor = Core.settings.getString("glopion-flavor", "Release-" + Version.buildString());
    public static String baseURL = Core.settings.getString("glopion-url", "https://raw.githubusercontent.com/o7-Fire/Mindustry-Glopion/main/");
    public static ClassLoader classLoader;
    public static Class<? extends Mod> unloaded = null;
    public static Mod loaded = null;
    public static boolean downloadThing = false;
    public static Bootstrapper bootstrapper;
    public static Fi jar;
    public static Main main;
    public static String classpath = "org.o7.Fire.Glopion.";
    
    static {
        classpath = classpath + flavor.replace('-', '.') + ".Main";
        Log.infoTag("Mindustry-Version", Version.buildString());
        Log.infoTag("Glopion-Bootstrapper", "Flavor: " + flavor);
        Log.infoTag("Glopion-Bootstrapper", "Classpath: " + classpath);
        load();
    }
    
    public Main() {
        main = this;
        bootstrapper = new Bootstrapper();
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
        
        String path = flavor.replace('-', '/') + "/" + Version.buildString() + ".jar";
        jar = Core.files.cache(path);
        if (jar.exists()){
            Log.infoTag("Glopion-Bootstrapper", "Loading: " + jar.absolutePath());
            try {
                classLoader = Vars.platform.loadJar(jar, "wtf ?");
                unloaded = (Class<? extends Mod>) Class.forName(classpath, true, classLoader);
            }catch(Exception e){
                handleException(e);
                return;
            }
        }else{
            Log.warn(jar.absolutePath() + " doesn't exist, loading in next startup to prevent game freeze");
            downloadThing = true;
        }
        
        
    }
    
    public static void handleException(Throwable e) {
        e.printStackTrace();
        Log.errTag("Glopion-Bootstrapper", e.toString());
        runOnUI(() -> Vars.ui.showException("Glopion-Bootstrapper Failed To Load", e));
        
        
    }
    
    public static void runOnUI(Runnable r) {
        if (Vars.ui != null && Vars.ui.loadfrag != null){
            r.run();
        }else{
            Events.on(EventType.ClientCreateEvent.class, cr -> r.run());
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
