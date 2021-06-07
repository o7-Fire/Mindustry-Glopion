package org.o7.Fire.Glopion.Module.Patch;

import org.o7.Fire.Glopion.Module.ModsModule;

public class ThisShitWillCrashAndUIGONE extends ModsModule {
    @Override
    public void start() {
        super.start();
        throw new RuntimeException("Cheese");
    }
}
