package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.Core;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import org.o7.Fire.Glopion.Internal.TextManager;
import org.o7.Fire.Glopion.UI.OptionsDialog;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
        if (!deepPatch && enableDeepPatchSettings && !test){
    
            if (GlopionDesktop.class.getClassLoader() instanceof URLClassLoader){
                Log.infoTag("DeepPatch", "Entering DeepPatch");
                URL[] urls = ((URLClassLoader) GlopionDesktop.class.getClassLoader()).getURLs();
                StringBuilder classPath = new StringBuilder(System.getProperty("java.class.path"));
                for (URL u : urls)
                    classPath.append(File.pathSeparator).append(u.getFile());
                File javaBin = new File(System.getProperty("java.home") + "/bin/java");
                String java = "java";
                if (javaBin.exists()) java = javaBin.getAbsolutePath();
                ArrayList<String> arg = new ArrayList<>();
                arg.add(java);
                if (Reflect.DEBUG_TYPE == Reflect.DebugType.UserPreference){
                    arg.add("-Ddev-user=1");
                }
                arg.add("-Dglopion-deepPatch=1");
                arg.add("-cr");
                arg.add(classPath.toString());
                arg.add("org.o7.Fire.Glopion.Premain.Run" + (Vars.headless ? "$Server" : ""));
                String[] cmd = new String[5];
                cmd = arg.toArray(cmd);
                Log.info(Arrays.toString(cmd));
                new ProcessBuilder(cmd).inheritIO().start();
                Threads.daemon(() -> {
                    try {
                        Thread.sleep(1000);//wait until child process spawned
                    }catch(InterruptedException e){
                    }
                    Core.app.exit();
                });
            }else {
                Log.warn(GlopionDesktop.class.getClassLoader().getClass() +" is not URLClassLoader, aborting DeepPatch");
            }
        
        }else if(deepPatch){
            Log.infoTag("DeepPatch", "Alive");
        }
    }
    
    @Override
    public void init() {
        TextManager.registerWords("enableDeepPatchSettings", "Deep Patch");
        
        super.init();
    }
}
