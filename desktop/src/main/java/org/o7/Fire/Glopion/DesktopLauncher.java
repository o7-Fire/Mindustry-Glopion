package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.util.Log;

public class DesktopLauncher extends GlopionCore {
    static {
        Log.debug("Invoked @ static ctr", DesktopLauncher.class);
        if (System.getProperty("glopion-deepPatch") == null){
            Log.infoTag("DeepPatch", "Entering DeepPatch");
            Reflect.restart("Premain.Run");
        }
        Log.infoTag("DeepPatch", "Alive");
    }
}
