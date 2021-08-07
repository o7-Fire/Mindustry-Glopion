package org.o7.Fire.Glopion.Brain;

import org.nd4j.common.config.ND4JClassLoading;
import org.o7.Fire.Glopion.GlopionDesktop;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Patch.UIPatch;

public class ND4JInterface extends ModsModule {
    {
        dependency.add(UIPatch.class);
    }
    @Override
    public void start() {
        super.start();
        ND4JClassLoading.setNd4jClassloader(GlopionDesktop.class.getClassLoader());
    
    
    }
}
