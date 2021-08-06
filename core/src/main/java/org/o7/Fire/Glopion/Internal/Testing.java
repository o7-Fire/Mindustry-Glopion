package org.o7.Fire.Glopion.Internal;

import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

import java.util.ArrayList;

public class Testing extends ModsModule {
    protected static final ArrayList<Boolean> sample = new ArrayList<>();
    protected static volatile int preInit = 0, init = 0, postInit = 0;
    
    public static boolean testCompleted() {
        sample.clear();
        ModuleRegisterer.invokeAll(ModsModule.class, s -> sample.add(s.isTestCompleted()));
        if (sample.contains(false)) return false;
        return true;
    }
    
    public static boolean isTestMode() {
        return System.getProperty("test") != null;
    }
    
    @Override
    public boolean disabled() {
        return !isTestMode();//isn't test mode
    }
    
    @Override
    public void preInit() throws Throwable {
        Log.info("TEST MODE");
        Log.warn("TEST MODE");
        Log.err("TEST MODE");
        preInit++;
    }
    
    @Override
    public void start() {
        Log.info("TEST MODE");
        Log.warn("TEST MODE");
        Log.err("TEST MODE");
        init++;
    }
    
    @Override
    public void update() {
        if (Vars.state.isPlaying() && testCompleted()){
            String stat = "preInit: " + preInit + ", Init: " + init + ", postInit: " + postInit;
            Core.app.post(Core.app::exit);
            if (init != 1 || preInit != 1 || postInit != 0){
                throw new RuntimeException("Runned more than once or not runned:\n" + stat);
            }
        }
    
    }
    
    @Override
    public void postInit() throws Throwable {
        postInit++;
    }
}
