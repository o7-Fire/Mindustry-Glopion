package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.util.Log;
import mindustry.mod.Mod;
import org.o7.Fire.Glopion.Internal.InformationCenter;

;

public class Core extends Mod {
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
