package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import Atom.Utility.Pool;
import arc.Core;
import arc.util.Log;
import mindustry.mod.Mod;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;
import org.o7.Fire.Glopion.UI.AtomicDialog;
import org.o7.Fire.Glopion.UI.OptionsDialog;
import org.o7.Fire.Glopion.UI.OzoneMenu;
import org.o7.Fire.Glopion.UI.WorldInformation;

import java.util.concurrent.Executors;

public class GlopionCore extends Mod {
    public static AtomicDialog modsMenu;
    public static boolean test;
    //public static CommandsListFrag commFrag;
    public static WorldInformation worldInformation;
    public static boolean colorPatchSettings;
    public static Class<? extends Mod> mainClass = GlopionCore.class;
    public static boolean blockDebugSettings;
    public static ModuleRegisterer moduleRegisterer;
    public static OzoneMenu glopionHud;
    
    static {
        if (Reflect.DEBUG_TYPE != Reflect.DebugType.None) Log.level = Log.LogLevel.debug;
        Log.debug("Debug: @", Reflect.DEBUG_TYPE);
        Log.debug("Invoked @ static ctr", GlopionCore.class);
        moduleRegisterer = new ModuleRegisterer();
        moduleRegisterer.core();
        if (Core.settings != null) OptionsDialog.load(k -> String.valueOf(Core.settings.get(k, null)));
        Pool.parallelAsync = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName(t.getName() + "-Atomic-Executor");
            t.setDaemon(true);
            return t;
        });
    }
    
    public GlopionCore() {
        Log.debug("Invoked @ ctr", GlopionCore.class);
        moduleRegisterer.preInit();
    }
    
    @Override
    public void init() {
        try {
            moduleRegisterer.init();
        }catch(Throwable t){
            WarningHandler.handleMindustry(t);
        }
        Log.infoTag("Glopion", "Loaded From " + InformationCenter.getCurrentJar());
        Log.infoTag("Glopion", "Class: " + this.getClass().getCanonicalName());
        
        
    }
}
