package org.o7.Fire.Glopion.Internal;

import arc.Core;
import arc.util.Log;
import org.o7.Fire.Glopion.Module.ModsModule;

public class Testing extends ModsModule {
    
    public static boolean isTestMode() {
        return System.getProperty("test") != null;
    }
    
    @Override
    public boolean disabled() {
        return !isTestMode();
    }
    
    @Override
    public void preInit() throws Throwable {
        Log.info("TEST MODE");
        Log.warn("TEST MODE");
        Log.err("TEST MODE");
    }
    
    @Override
    public void start() {
        Log.info("TEST MODE");
        Log.warn("TEST MODE");
        Log.err("TEST MODE");
    }
    
    @Override
    public void postInit() throws Throwable {
        Core.app.post(Core.app::exit);
    }
}
