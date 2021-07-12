package org.o7.Fire.Glopion.Brain;

import freemarker.log._Log4jLoggerFactory;
import mindustry.Vars;
import org.bytedeco.javacpp.tools.Slf4jLogger;
import org.nd4j.common.config.ND4JClassLoading;
import org.o7.Fire.Glopion.GlopionDesktop;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.Patch.UIPatch;
import org.o7.Fire.Glopion.UI.ModsMenu;
import org.o7.Fire.Glopion.UI.ND4JDialog;

public class ND4JInterface extends ModsModule {
    {
        dependency.add(UIPatch.class);
    }
    @Override
    public void init() {
        ND4JClassLoading.setNd4jClassloader(GlopionDesktop.class.getClassLoader());
        if(!Vars.headless)
        ModsMenu.add(new ND4JDialog());
    }
}
