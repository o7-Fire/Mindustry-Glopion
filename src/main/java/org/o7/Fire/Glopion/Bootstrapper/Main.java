package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.mod.Mod;

public class Main extends Mod {
    public final static String baseURL = Core.settings.getString("glopion-url", "https://raw.githubusercontent.com/o7-Fire/Mindustry-Glopion/main/release/");
    public static String url = null;
    public static String flavor = Core.settings.getString("glopion-flavor", "Release");
    public static ClassLoader classLoader;
    public static Class<? extends Mod> unloaded = null;
    public static Mod loaded = null;
    public static boolean downloadThing = false;
    public static Bootstrapper bootstrapper;
    public static Fi jar;
    public static Main main;
    
    static {
        flavor = flavor + "-";
        flavor = flavor + (Vars.android ? "Android" : "Desktop");
        Log.infoTag("Mindustry-Version", Version.buildString());
        Log.infoTag("Glopion-Bootstrapper", "Flavor: " + flavor);
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
    
    public static void load() {
        if (System.getProperty("glopion.loaded", "0").equals("1")){
            Log.errTag("Glopion-Bootstrapper", "Trying to load multiple times !!!");
            return;
        }
        System.setProperty("glopion.loaded", "1");
        
        String path = flavor.replace('-', '/') + "/" + Version.buildString() + ".jar";
        url = baseURL + path;
        jar = Core.files.cache(path);
        if (jar.exists()){
            Log.infoTag("Glopion-Bootstrapper", "Loading: " + jar.absolutePath());
            try {
                classLoader = Vars.platform.loadJar(jar, "wtf ?");
                unloaded = (Class<? extends Mod>) Class.forName(("org.o7.Fire.Glopion" + flavor.replace('-', '.') + ".Main"), true, classLoader);
            }catch(Exception e){
                handleException(e);
                return;
            }
        }else{
            Log.warn(jar.absolutePath() + " doesn't exist");
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
