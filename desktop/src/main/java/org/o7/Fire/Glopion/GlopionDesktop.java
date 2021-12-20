package org.o7.Fire.Glopion;

import Atom.Reflect.Reflect;
import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Internal.TextManager;
import org.o7.Fire.Glopion.Premain.Run;
import org.o7.Fire.Glopion.UI.OptionsDialog;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

public class GlopionDesktop extends GlopionCore {
    public static boolean deepPatch = System.getProperty("DeepPatch") != null, enableDeepPatchSettings = false, fooClientDeepPatchSettings = false;
    
    static {
        Log.debug("Invoked @ static ctr", GlopionDesktop.class);
    }
    
    //oh
    @Override
    public void preInit() throws Throwable {
        
        Log.debug("Invoked @ preInit", GlopionDesktop.class.getCanonicalName());
        OptionsDialog.classSettings.add(GlopionDesktop.class);
        
        super.preInit();
        //need to place it in here, assuming its exists it's going to be used for Deep Patch
        try {
            SharedBootstrapper.addDependency("Foo-Client",
                    InformationCenter.glopionBootstrapperConfig.getProperty("fooClient"));
            if (InformationCenter.fooClasspathLocation == null){
                InformationCenter.fooClasspathLocation = SharedBootstrapper.downloadFile.get("Foo-Client");
            }
        }catch(MalformedURLException e){
            Log.err("Foo-Client Malformed URL", e);
        }
        
        if (!deepPatch && enableDeepPatchSettings && !test){
            
            if (GlopionDesktop.class.getClassLoader() instanceof URLClassLoader){
                Log.infoTag("DeepPatch", "Entering DeepPatch");
                URL[] urls = ((URLClassLoader) GlopionDesktop.class.getClassLoader()).getURLs();
                StringBuilder classPath = new StringBuilder();
                if (fooClientDeepPatchSettings){
                    
                    if (InformationCenter.fooClasspathLocation != null && InformationCenter.fooClasspathLocation.exists()){
                        classPath.append(InformationCenter.fooClasspathLocation).append(File.pathSeparator);
                    }else{
                        Log.err("Foo-Client classpath location does not exist: " + InformationCenter.fooClasspathLocation);
                    }
                }
                //the classpath priority is sorted by the order of the urls so we good
                classPath.append(System.getProperty("java.class.path"));
                for (URL u : urls) {
                    classPath.append(File.pathSeparator).append(u.getFile());
                }
                File javaBin = new File(System.getProperty("java.home") + "/bin/java");
                String java = "java";
                if (javaBin.exists()) java = javaBin.getAbsolutePath();
                ArrayList<String> arg = new ArrayList<>();
                arg.add(java);
                if (Reflect.DEBUG_TYPE == Reflect.DebugType.UserPreference){
                    arg.add("-Ddev-user=1");
                }
                arg.add("-Dglopion-deepPatch=1");
                arg.add("-cp");
                arg.add(classPath.toString());
                
                arg.add(Vars.headless ? Run.Server.class.getName() : Run.class.getName());
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
        if (InformationCenter.glopionBootstrapperConfig.getProperty("fooClient") != null && !Vars.headless){
        
        
            TextManager.registerWords("fooClientDeepPatchSettings",
                    "Use Foo-Client instead current Mindustry client for Deep Patch (don't check this if its already foo client)");
            Events.on(OptionsDialog.Events.BooleanSettingsChanged.class, e -> {
                if (e.field.getName().equals("fooClientDeepPatchSettings")){
                
                    if (e.newSettings && !SharedBootstrapper.downloadFile.get("Foo-Client").exists()){
                        Interface.showInfo("Foo-Client",
                                "Foo-Client not found, please download it from Settings>Game>Dependency");
                    }
                }
            });
        
        }
        super.init();
    }
}
