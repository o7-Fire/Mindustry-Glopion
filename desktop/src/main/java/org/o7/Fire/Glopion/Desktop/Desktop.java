package org.o7.Fire.Glopion.Desktop;

import Atom.Reflect.Reflect;
import arc.util.Log;
import org.o7.Fire.Glopion.Core;

public class Desktop extends Core {
    static {
        if (System.getProperty("glopion-deepPatch") == null){
            Log.infoTag("DeepPatch", "Entering DeepPatch");
            Reflect.restart("Premain.Run");
        }
        Log.infoTag("DeepPatch", "Alive");
    }
}
