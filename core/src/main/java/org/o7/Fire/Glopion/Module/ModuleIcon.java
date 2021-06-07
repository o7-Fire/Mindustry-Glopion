package org.o7.Fire.Glopion.Module;

public class ModuleIcon extends ModsModule {
    @Override
    public void postInit() {
        ModuleRegisterer.invokeAll(m -> m.thisLoaded.iconTexture = m.getIcon());
    }
}
