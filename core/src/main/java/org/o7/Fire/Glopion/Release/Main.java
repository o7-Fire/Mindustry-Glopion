package org.o7.Fire.Glopion.Release;

import arc.util.Log;
import mindustry.mod.Mod;
import org.o7.Fire.Glopion.Internal.InformationCenter;

public class Main extends Mod {
    @Override
    public void init() {
        Log.infoTag("Glopion", "Hello From " + InformationCenter.getCurrentJar());
        Log.infoTag("Glopion", "Class: " + this.getClass().getCanonicalName());
    }
}
