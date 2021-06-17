package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import arc.Events;
import arc.files.Fi;
import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.ClientLauncher;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.desktop.DesktopLauncher;
import mindustry.game.EventType;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Scripts;
import mindustry.net.Net;
import mindustry.type.Publishable;
import net.jpountz.util.Native;
import rhino.Context;

public class MindustryLauncher {
  
    public static void main(String[] args) {
        if (System.getProperty("dev") != null){
            Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
            Reflect.debug = true;
            
        }
      
        if(Reflect.debug){
            System.out.println("Mindustry Jar Classloader: "+MindustryLauncher.class.getClassLoader().getClass().getCanonicalName());
            System.out.println("Current Jar Classloader: " + ModClassLoader.class.getClassLoader().getClass().getCanonicalName());
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
     * i use Intellij breakpoint evaluate or just do some dark magic
     * */
    public static void patchClassloader(){
        Platform inert = Vars.platform;
       
        Vars.platform = new Platform() {
            @Override
            public ClassLoader loadJar(Fi jar, ClassLoader parent) throws Exception {
                if(jar.absolutePath().contains("Glopion")){
                    Log.info("Found The Glopion, try using system classloader");
                    return MindustryLauncher.class.getClassLoader();//we do some little trolling
                }else{
                    return inert.loadJar(jar, parent);
                }
            }
    
            @Override
            public void updateLobby() {
                inert.updateLobby();
            }
    
            @Override
            public void inviteFriends() {
                inert.inviteFriends();
            }
    
            @Override
            public void publish(Publishable pub) {
                inert.publish(pub);
            }
    
            @Override
            public void viewListing(Publishable pub) {
                inert.viewListing(pub);
            }
    
            @Override
            public void viewListingID(String mapid) {
                inert.viewListingID(mapid);
            }
    
            @Override
            public Seq<Fi> getWorkshopContent(Class<? extends Publishable> type) {
                return inert.getWorkshopContent(type);
            }
    
            @Override
            public void openWorkshop() {
                inert.openWorkshop();
            }
    
            @Override
            public Net.NetProvider getNet() {
                return inert.getNet();
            }
    
            @Override
            public Scripts createScripts() {
                return inert.createScripts();
            }
    
            @Override
            public Context getScriptContext() {
                return inert.getScriptContext();
            }
    
            @Override
            public void updateRPC() {
                inert.updateRPC();
            }
    
            @Override
            public String getUUID() {
                return inert.getUUID();
            }
    
            @Override
            public void shareFile(Fi file) {
                inert.shareFile(file);
            }
    
            @Override
            public void export(String name, String extension, FileWriter writer) {
                inert.export(name, extension, writer);
            }
    
            @Override
            public void showFileChooser(boolean open, String title, String extension, Cons<Fi> cons) {
                inert.showFileChooser(open, title, extension, cons);
            }
    
            @Override
            public void showFileChooser(boolean open, String extension, Cons<Fi> cons) {
                inert.showFileChooser(open, extension, cons);
            }
    
            @Override
            public void showMultiFileChooser(Cons<Fi> cons, String... extensions) {
                inert.showMultiFileChooser(cons, extensions);
            }
    
            @Override
            public void hide() {
                inert.hide();
            }
    
            @Override
            public void beginForceLandscape() {
                inert.beginForceLandscape();
            }
    
            @Override
            public void endForceLandscape() {
                inert.endForceLandscape();
            }
        };
        
    }
}
