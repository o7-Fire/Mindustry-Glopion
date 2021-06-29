package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.util.Log;
import org.o7.Fire.Glopion.Patch.Translation;
import org.o7.Fire.Glopion.UI.OptionsDialog;

public class GlopionDesktop extends GlopionCore {
    public static boolean deepPatch = System.getProperty("DeepPatch") != null, enableDeepPatchSettings = false;
    static {
        Log.debug("Invoked @ static ctr", GlopionDesktop.class);
    }
    //oh
    @Override
    public void preInit() throws Throwable {
        Log.debug("Invoked @ preInit", GlopionDesktop.class.getCanonicalName());
        OptionsDialog.classSettings.add(GlopionDesktop.class);
   
        super.preInit();
        if (!deepPatch && enableDeepPatchSettings){
            Log.infoTag("DeepPatch", "Entering DeepPatch");
            Reflect.restart("Premain.Run");
        }else if(deepPatch){
            Log.infoTag("DeepPatch", "Alive");
        }
    }
    
    @Override
    public void init() {
        Translation.registerWords("enableDeepPatchSettings", "Deep Patch");
        super.init();
    }
}
