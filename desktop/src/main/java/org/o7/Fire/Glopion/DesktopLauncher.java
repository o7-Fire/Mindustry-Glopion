package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.util.Log;

public class DesktopLauncher extends GlopionCore {
    static {
        if (System.getProperty("glopion-deepPatch") == null){
            Log.infoTag("DeepPatch", "Entering DeepPatch");
            Reflect.restart("Premain.Run");
        }
        Log.infoTag("DeepPatch", "Alive");
    }
}
