package org.o7.Fire.Glopion.Desktop;

import mindustry.Vars;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Patch.UIPatch;

public class DesktopUI extends ModsModule {
    
    {
        dependency.add(UIPatch.class);
    }
    
    @Override
    public boolean disabled() {
        return Vars.ui == null;
    }
    
    @Override
    public void start() {
        super.start();

    }
}
