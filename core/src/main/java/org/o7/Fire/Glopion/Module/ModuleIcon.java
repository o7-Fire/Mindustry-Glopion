package org.o7.Fire.Glopion.Module;

public class ModuleIcon extends ModsModule {
    @Override
    public void postInit() {
        ModuleRegisterer.invokeAll(ModsModule.class, m -> m.thisLoaded.iconTexture = m.getIcon());
    }
}
