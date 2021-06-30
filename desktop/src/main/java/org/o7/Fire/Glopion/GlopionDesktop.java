package org.o7.Fire.Glopion;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.game.EventType;
import org.o7.Fire.Glopion.Patch.Translation;
import org.o7.Fire.Glopion.UI.OptionsDialog;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

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
         
            if(GlopionDesktop.class.getClassLoader() instanceof URLClassLoader){
                Log.infoTag("DeepPatch", "Entering DeepPatch");
                URL[] urls = ((URLClassLoader) GlopionDesktop.class.getClassLoader()).getURLs();
                StringBuilder classPath = new StringBuilder(System.getProperty("java.class.path"));
                for(URL u : urls)
                    classPath.append(File.pathSeparator).append(u.getFile());
                File javaBin = new File(System.getProperty("java.home") + "/bin/java");
                String java = "java";
                if (javaBin.exists()) java = javaBin.getAbsolutePath();
                String[] cmd = new String[]{java, "-Dglopion-deepPatch=1", "-cp", classPath.toString(), "org.o7.Fire.Glopion.Premain.Run"};
                Log.info(Arrays.toString(cmd));
                new ProcessBuilder(cmd).inheritIO().start();
                Runtime.getRuntime().exec(cmd);
                Events.on(EventType.ClientLoadEvent.class,s->Core.app.exit());
            }else {
                Log.warn(GlopionDesktop.class.getClassLoader().getClass() +" is not URLClassLoader, aborting DeepPatch");
            }
        
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
