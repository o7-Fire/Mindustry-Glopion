package org.o7.Fire.Glopion.Desktop;

import Atom.Reflect.Reflect;
import arc.util.Log;
import org.o7.Fire.Glopion.GlopionCore;

public class Desktop extends GlopionCore {
    static {
        if (System.getProperty("glopion-deepPatch") == null){
            Log.infoTag("DeepPatch", "Entering DeepPatch");
            Reflect.restart("Premain.Run");
        }
        Log.infoTag("DeepPatch", "Alive");
    }
}
