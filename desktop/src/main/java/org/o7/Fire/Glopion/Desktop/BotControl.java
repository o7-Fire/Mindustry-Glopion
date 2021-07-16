package org.o7.Fire.Glopion.Desktop;

import arc.util.Log;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.WorldModule;

public class BotControl extends ModsModule implements WorldModule {
    public Type currentType = null;
    public Mode currentMode = null;
    
    @Override
    public void onWoldUnload() {
        currentMode = null;
        currentType = null;
    }
    
    public boolean running() {
        return currentType != null && currentMode != null;
    }
    
    @Override
    public void update() {
    
    }
    
    public void control(Mode mode, Type type) {
        if (running()){
            Log.info("Already Running @, @", currentMode, currentType);
            return;
        }
        currentType = type;
        currentMode = mode;
    }
    
    @Override
    public void init() {
    
    }
    
    public enum Mode {Play, Train, Test}
    
    public enum Type {PVP}
}
