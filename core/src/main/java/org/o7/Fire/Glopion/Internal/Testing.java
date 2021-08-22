package org.o7.Fire.Glopion.Internal;

import Atom.Time.Timer;
import arc.Core;
import arc.util.Log;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Testing extends ModsModule {
    protected static final ArrayList<Boolean> sample = new ArrayList<>();
    protected static volatile int preInit = 0, init = 0, postInit = 0;
    protected static final Timer timer = new Timer(TimeUnit.SECONDS, 5);
    
    {
        testCompleted = false;
    }
    
    public static boolean testCompleted() {
        sample.clear();
        boolean log = timer.get();
        ModuleRegisterer.invokeAll(ModsModule.class, s -> {
            boolean b = s.isTestCompleted();
            sample.add(b);
            if (!b && log) Log.infoTag(s.getName() + "-Test", "Waiting for test to finish");
        });
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
    
    
    public void onFinish() {
        System.err.println("--------------------");
        System.err.println(WarningHandler.errorList.size() + " Errors found");
        for (Throwable t : WarningHandler.errorList) {
            t.printStackTrace();
            System.out.println();
        }
        
    }
    
    @Override
    public void preInit() throws Throwable {
        Log.info("TEST MODE");
        Log.warn("TEST MODE");
        Log.err("TEST MODE");
        preInit++;
        assert isTestMode() : "Not test mode but initliazed";
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
        if (testCompleted()){
            String stat = "preInit: " + preInit + ", Init: " + init + ", postInit: " + postInit;
    
            if (init != 1 || preInit != 1 || postInit != 1){
                throw new RuntimeException("Runned more than once or not runned:\n" + stat);
            }
            onFinish();
            assert WarningHandler.errorList.size() == 0 : "There is error";
            Core.app.exit();
        }
    
    }
    
    @Override
    public void postInit() throws Throwable {
        postInit++;
        testCompleted = true;
    }
}
