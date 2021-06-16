package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import arc.files.Fi;
import arc.util.Log;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.desktop.DesktopLauncher;
import mindustry.mod.ModClassLoader;
import net.jpountz.util.Native;

public class MindustryLauncher {
    static DIWHYClassloader diwhyClassloader = (DIWHYClassloader) MindustryLauncher.class.getClassLoader();
    public static void main(String[] args) {
        if (System.getProperty("dev") != null){
            Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
            Reflect.debug = true;
            
        }
      
        if(Reflect.debug){
            System.out.println(MindustryLauncher.class.getClassLoader().getClass().getCanonicalName());
            System.out.println(ModClassLoader.class.getClassLoader().getClass().getCanonicalName());
        }
        try {
            Native.load();
        }catch(Throwable t){
            System.out.println(t);
        }
        DesktopLauncher.main(args);
    }
    
    /**  patch after Vars.mods {@link ClientLauncher#setup()} to gain classloader control over mods
     * i use Intellij breakpoint evaluate
     * */
    public static void patchClassloader(){
        Platform inert = new Platform() {};
        Vars.platform = new Platform() {
            @Override
            public ClassLoader loadJar(Fi jar, ClassLoader parent) throws Exception {
                if(jar.name().contains("Glopion")){
                    Log.info("Found The Glopion, try using system classloader");
                    diwhyClassloader.addURL(jar.file().toURI().toURL());
                    return diwhyClassloader;
                }else return inert.loadJar(jar,parent);
            }
        };
    }
}
