package org.o7.Fire.Glopion.Desktop;

import mindustry.Vars;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Patch.UIPatch;
import org.o7.Fire.Glopion.UI.ModsMenu;
import org.o7.Fire.Glopion.UI.ND4JDialog;
import org.o7.Fire.Glopion.UI.OpenCVDialog;

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
        ModsMenu.add(new OpenCVDialog());
        ModsMenu.add(new ND4JDialog());
    }
}
