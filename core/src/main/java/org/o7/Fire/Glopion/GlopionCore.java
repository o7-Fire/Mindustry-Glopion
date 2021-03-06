package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import Atom.Time.Time;
import Atom.Utility.Pool;
import android.os.Build;
import arc.Core;
import arc.util.Log;
import mindustry.client.Client;
import mindustry.mod.Mod;
import mindustry.mod.Plugin;
import org.o7.Fire.Glopion.Brain.Classification.ImageClassifier;
import org.o7.Fire.Glopion.Control.GlopionControl;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.Testing;
import org.o7.Fire.Glopion.Internal.TextManager;
import org.o7.Fire.Glopion.Module.Module;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;
import org.o7.Fire.Glopion.UI.AtomicDialog;
import org.o7.Fire.Glopion.UI.HudMenu;
import org.o7.Fire.Glopion.UI.OptionsDialog;
import org.o7.Fire.Glopion.UI.WorldInformation;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GlopionCore extends Plugin implements Module {
    static {
        validator();
    }
    
    public static AtomicDialog modsMenu;
    public static GlopionControl glopionControl = new GlopionControl();
    public static final boolean test = Testing.isTestMode();
    //public static CommandsListFrag commFrag;
    public static WorldInformation worldInformation;
    public static Class<? extends Mod> mainClass = GlopionCore.class;
    public static boolean blockDebugSettings, machineVisualizeRenderSettings, colorPatchSettings, debugSettings, translateChatSettings,//kys
            interceptChatThenTranslateSettings, rebroadcastTranslatedMessageSettings, censorInapproriatePictureSettings;
    public static String nsfwJsUrlSettings = "https://nsfw-detector-o7.herokuapp.com/";
    public static ModuleRegisterer moduleRegisterer;
    public static HudMenu glopionHud;
    public static AtomicDialog machineInformation;
    public static final Time startTime = new Time(TimeUnit.MILLISECONDS);
    public static Time loadFinishedTime = null;
    public static ImageClassifier imageClassifier = null;
    public static final boolean fooClient;

    static {
        boolean fooClient1;
        try {
            Client.INSTANCE.getTimer();//stub
            fooClient1 = true;
        }catch(Error e){
            fooClient1 = false;
        }
        fooClient = fooClient1;
        if (debugSettings && Reflect.debug) {
            Reflect.DEBUG_TYPE = Reflect.DebugType.UserPreference;
         }
        if (Reflect.DEBUG_TYPE != Reflect.DebugType.None) Log.level = Log.LogLevel.debug;
        Log.debug("Debug: @", Reflect.DEBUG_TYPE);
        Log.debug("Invoked @ static ctr", GlopionCore.class);

    }
    
    public GlopionCore() {
        Log.debug("Invoked @ ctr", GlopionCore.class);
        try {
            preInit();
        }catch(Throwable throwable){
            WarningHandler.handleMindustry(throwable);
        }
        
        
    }
    
    static void validator() {
        if (System.getProperty(GlopionCore.class.getCanonicalName()) != null){
            throw new IllegalStateException("GlopionCore is already loaded");
        }
        System.setProperty(GlopionCore.class.getCanonicalName(), "true");
        //get android api version
        try {
            int current = Build.VERSION.SDK_INT;
            int required = Integer.parseInt(InformationCenter.glopionBootstrapperConfig.getProperty("minAndroidApi"));
            if (current < required){
                throw new IllegalStateException("Glopion requires Android API level " + required + " or higher");
            }
        }catch(Throwable throwable){
            if (throwable instanceof IllegalStateException){
                throw (IllegalStateException) throwable;
            }
        }
    }
    
    public Time getLoadingTime() {
        Time finished = loadFinishedTime;
        if (finished == null) finished = new Time(TimeUnit.MILLISECONDS);
        return startTime.elapsed(finished);
    }
    
    @Override
    public void preInit() throws Throwable {
        Log.debug("Invoked @ preInit", GlopionCore.class.getCanonicalName());
        moduleRegisterer = new ModuleRegisterer();
        moduleRegisterer.core();
        OptionsDialog.classSettings.add(GlopionCore.class);
        if (Core.settings != null) OptionsDialog.load(k -> Core.settings.getString(k, null));
        
        Pool.parallelAsync = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName(t.getName() + "-Atomic-Executor");
            t.setDaemon(true);
            return t;
        });
        moduleRegisterer.preInit();
    }
    
    @Override
    public void init() {
        //this tbh
        TextManager.registerWords("nsfwJsUrlSettings", "NSFW JS Provider[WIP]");
        TextManager.registerWords("censorInapproriatePictureSettings",
                "Censor inappropriate pixel art/logic display[WIP]");
        TextManager.registerWords("machineVisualizeRenderSettings", "Machine Recorder Visualization");
        TextManager.registerWords("translateChatSettings",
                "Translate Chat (Client GUI Only, async, unglyph, uncolorized, duplicating, skip already translated)");
        TextManager.registerWords("interceptChatThenTranslateSettings",
                "Intercept Player Message Then Translate It (Server Only, may cause lag, send to everyone)");
        TextManager.registerWords("rebroadcastTranslatedMessageSettings",
                "Translate message, then send it again to all player (same as Translate Chat except for Server Only, and resend to everyone)");
        try {
            moduleRegisterer.init();
        }catch(Throwable t){
            WarningHandler.handleMindustry(t);
        }
        Log.infoTag("Glopion", "Loaded From " + InformationCenter.getCurrentJar());
        Log.infoTag("Glopion", "Class: " + this.getClass().getCanonicalName());
    
    
    }
}
