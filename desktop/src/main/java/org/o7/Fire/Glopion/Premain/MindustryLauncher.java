package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import arc.Events;
import arc.Files;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.SdlConfig;
import arc.files.Fi;
import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Structs;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.desktop.DesktopLauncher;
import mindustry.game.EventType;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Scripts;
import mindustry.net.Net;
import mindustry.type.Publishable;
import net.jpountz.util.Native;
import org.o7.Fire.Glopion.Dev.ModsClassHook;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import rhino.Context;

import java.lang.reflect.Method;

public class MindustryLauncher {
    
    
    static Method handleCrashMethod;
    static Runnable varsInitMethodLineNo312ListenerHijacker;
    
    public static void handleCrash(Throwable e) {
        if (handleCrashMethod == null){
            try {
                handleCrashMethod = DesktopLauncher.class.getDeclaredMethod("handleCrash", Throwable.class);
                handleCrashMethod.setAccessible(true);
            }catch(ReflectiveOperationException ignored){}
        }
        if (handleCrashMethod != null){
            try {
                handleCrashMethod.invoke(null, e);
            }catch(ReflectiveOperationException ignored){
            
            }
        }
        WarningHandler.handleMindustry(e);
    }
    
    public static void main(String[] args) {
        if (System.getProperty("dev-user") != null){
            Reflect.DEBUG_TYPE = Reflect.DebugType.UserPreference;
            Reflect.debug = true;
        }
        
        if (System.getProperty("dev") != null){
            Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
            Reflect.debug = true;
            MindustryLauncher.loadWithoutFile();
        }
    
        if (Reflect.debug){
            System.out.println("Mindustry Jar Classloader: " + MindustryLauncher.class.getClassLoader()
                    .getClass()
                    .getCanonicalName());
            System.out.println("Current Jar Classloader: " + ModClassLoader.class.getClassLoader()
                    .getClass()
                    .getCanonicalName());
            registerPatcher();
        }
        try {
            Native.load();
        }catch(Throwable t){
            Log.warn("Failed to load LZ4Factory-Native, multiplayer performance may be degraded: @", t);
            //godammn why load factory
        }
        try {
            Vars.loadLogger();
            new SdlApplication(new DesktopLauncher(args), new SdlConfig() {
                {
                    this.title = "Mindustry-Glopion";
                    this.maximized = true;
                    this.width = 900;
                    this.height = 700;
                    this.gl30 = !Structs.contains(args, "-nogl3");
                    
                    if (Structs.contains(args, "-debug") || Reflect.debug){
                        Log.level = Log.LogLevel.debug;
                    }
                    
                    this.setWindowIcon(Files.FileType.internal, new String[]{"icons/icon_64.png"});
                }
                
            }) {
                @Override
                public boolean isAndroid() {
                    MindustryLauncher.hijacker();
                    return super.isAndroid();
                }
            };
        }catch(Throwable t){
            handleCrash(t);
        }
        
    }
    
    static void loadWithoutFile() {
        MindustryLauncher.varsInitMethodLineNo312ListenerHijacker = () -> {
            MindustryLauncher.hookModsLoader();
            MindustryLauncher.modsClassHook.load("org.o7.Fire.Glopion.GlopionDesktop");
            System.err.println("GlopionDesktop loaded via reflection");
        };
    }
    
    /**
     * This is a hack to make the Vars.init() method to be called after the Vars.mods are loaded.
     * to load the mods without placing files in the mods folder.
     * Development usage only
     * Max 1 hops
     */
    static void hijacker() {
        if (varsInitMethodLineNo312ListenerHijacker != null){
            StackTraceElement stackTraceElement = Reflect.getCallerClassStackTrace(1);
            if (stackTraceElement.getFileName() != null && stackTraceElement.getFileName()
                    .equals("Vars.java") && stackTraceElement.getMethodName().equals("init")){
                varsInitMethodLineNo312ListenerHijacker.run();
                varsInitMethodLineNo312ListenerHijacker = null;
            }
        }
    }
    
    public static void registerPatcher() {
        //Use ClientCreateEvent as Logger is not initialized
        Events.on(EventType.ClientCreateEvent.class, s -> {
            //at this point Vars.platform != this
            //so we need to wait a few more line
            final Log.LogHandler original = Log.logger;
            Log.logger = (e, a) -> {
                if (a.startsWith("[GL] Version:")){
                    //Vars.platform == this
                    //patch time
                    patchClassloader(Vars.platform);
                    //return to original to avoid conflict, or something
                    Log.logger = original;
                }
                original.log(e, a);
            };
        });
    }

    static ModsClassHook modsClassHook;

    static void hookModsLoader() {
        if (Vars.mods != null) {
            try {
                modsClassHook = new ModsClassHook(Vars.mods);
            } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
                throw new IllegalStateException("Failed to hook mods loader", e);
            }
        } else {
            throw new IllegalStateException("Vars.mods is null");
        }
    }

    /**
     * patch after Vars.platform = this {@link ClientLauncher#setup()} to gain classloader control over mods
     * i use Intellij breakpoint evaluate or just do some dark magic
     */
    public static void patchClassloader(Platform inert) {

        Vars.platform = new Platform() {
            @Override
            public ClassLoader loadJar(Fi jar, ClassLoader parent) throws Exception {
                if (jar.absolutePath().contains("Glopion")) {
                    Log.info("Found The Glopion, try using system classloader");
                    return MindustryLauncher.class.getClassLoader();//we do some little trolling
                }else{
                    return inert.loadJar(jar, parent);
                }
            }
    
            @Override
            public void updateLobby() {
                inert.updateLobby();
            }
    
            @Override
            public void inviteFriends() {
                inert.inviteFriends();
            }
    
            @Override
            public void publish(Publishable pub) {
                inert.publish(pub);
            }
    
            @Override
            public void viewListing(Publishable pub) {
                inert.viewListing(pub);
            }
    
            @Override
            public void viewListingID(String mapid) {
                inert.viewListingID(mapid);
            }
    
            @Override
            public Seq<Fi> getWorkshopContent(Class<? extends Publishable> type) {
                return inert.getWorkshopContent(type);
            }
    
            @Override
            public void openWorkshop() {
                inert.openWorkshop();
            }
    
            @Override
            public Net.NetProvider getNet() {
                return inert.getNet();
            }
    
            @Override
            public Scripts createScripts() {
                return inert.createScripts();
            }
    
            @Override
            public Context getScriptContext() {
                return inert.getScriptContext();
            }
    
            @Override
            public void updateRPC() {
                inert.updateRPC();
            }
    
            @Override
            public String getUUID() {
                return inert.getUUID();
            }
    
            @Override
            public void shareFile(Fi file) {
                inert.shareFile(file);
            }
    
            @Override
            public void export(String name, String extension, FileWriter writer) {
                inert.export(name, extension, writer);
            }
    
            @Override
            public void showFileChooser(boolean open, String title, String extension, Cons<Fi> cons) {
                inert.showFileChooser(open, title, extension, cons);
            }
    
            @Override
            public void showFileChooser(boolean open, String extension, Cons<Fi> cons) {
                inert.showFileChooser(open, extension, cons);
            }
    
            @Override
            public void showMultiFileChooser(Cons<Fi> cons, String... extensions) {
                inert.showMultiFileChooser(cons, extensions);
            }
    
            @Override
            public void hide() {
                inert.hide();
            }
    
            @Override
            public void beginForceLandscape() {
                inert.beginForceLandscape();
            }
    
            @Override
            public void endForceLandscape() {
                inert.endForceLandscape();
            }
        };
        
    }
}
