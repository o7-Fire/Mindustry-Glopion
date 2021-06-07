package org.o7.Fire.Glopion.Module;

public class ModuleIcon extends ModsModule {
    @Override
    public void start() {
        ModuleRegisterer.invokeAll(m -> m.thisLoaded.iconTexture = m.getIcon());
    }
}
