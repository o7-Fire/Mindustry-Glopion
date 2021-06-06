package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.util.Log;
import mindustry.mod.Mod;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.UI.AtomicDialog;
import org.o7.Fire.Glopion.UI.EnvironmentInformation;

public class GlopionCore extends Mod {
    public static AtomicDialog modsMenu;
    public static boolean test;
    //public static CommandsListFrag commFrag;
    public static EnvironmentInformation worldInformation;
    public static boolean colorPatch;
    public static Class<? extends Mod> mainClass = GlopionCore.class;
    public static boolean blockDebug;
    
    static {
        if (Reflect.DEBUG_TYPE != Reflect.DebugType.None) Log.level = Log.LogLevel.debug;
        Log.debug("Debug: @", Reflect.DEBUG_TYPE);
    }
    
    @Override
    public void init() {
        Log.infoTag("Glopion", "Loaded From " + InformationCenter.getCurrentJar());
        Log.infoTag("Glopion", "Class: " + this.getClass().getCanonicalName());
        
    }
}
