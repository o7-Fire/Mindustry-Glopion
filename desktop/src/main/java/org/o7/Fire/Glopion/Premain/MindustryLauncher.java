package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import arc.Events;
import arc.files.Fi;
import arc.util.Log;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.desktop.DesktopLauncher;
import mindustry.game.EventType;
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
            registerPatcher();
        }
        try {
            Native.load();
        }catch(Throwable t){
            Log.warn("Failed to load LZ4Factory Native online multiplayer performance may be degraded: @",t);
        }
        DesktopLauncher.main(args);
    }
    public static void registerPatcher(){
        //Use ClientCreateEvent as Logger is not initialized
        Events.on(EventType.ClientCreateEvent.class,s->{
            //at this point Vars.platform != this
            //so we need to wait a few more line
            Log.LogHandler original = Log.logger;
            Log.logger = (e, a) ->{
                if(a.startsWith("[GL] Version:")){
                    //Vars.platform == this
                    //patch time
                    patchClassloader();
                    //return to original to avoid conflict
                    Log.logger = original;
                }
                original.log(e,a);
            };
        });
    }
    /**  patch after Vars.platform = this {@link ClientLauncher#setup()} to gain classloader control over mods
     * i use Intellij breakpoint evaluate
     * */
    public static void patchClassloader(){
        Platform inert = Vars.platform;
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
