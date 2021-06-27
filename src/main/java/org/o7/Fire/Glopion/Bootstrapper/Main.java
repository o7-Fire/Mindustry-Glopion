package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.input.KeyCode;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.BaseDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static mindustry.Vars.mobile;
import static org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper.*;

public class Main extends Mod {
    public static String flavor = Core.settings.getString("glopion-flavor", "Release-" + Version.buildString());
    public static String baseURL = Core.settings.getString("glopion-url", "https://raw.githubusercontent.com/o7-Fire/Mindustry-Glopion/main/");
    public static ClassLoader classLoader;
    public static Class<? extends Mod> unloaded = null;
    public static Mod loaded = null;
    public static boolean downloadThing = false;
    public static BootstrapperUI bootstrapper;
    public static Fi jar;
    public static Main main;
    public static String classpath = "org.o7.Fire.Glopion.";
    public static String info = "None";
    public static final ArrayList<Throwable> error = new ArrayList<>();
    

    
    public Main() {
        classpath = classpath + flavor.split("-")[0] + "Launcher";
        Log.infoTag("Mindustry-Version", Version.buildString());
        Log.infoTag("Mindustry-Version-Combined", Version.combined());
        Log.infoTag("Glopion-Bootstrapper", "Flavor: " + flavor);
        Log.infoTag("Glopion-Bootstrapper", "Classpath: " + classpath);
        try {
            load();
        }catch(Throwable t){
            handleException(t);
        }
        main = this;
        bootstrapper = new BootstrapperUI();
        if (unloaded != null){
            try {
                loaded = unloaded.getDeclaredConstructor().newInstance();
            }catch(Exception e){
                handleException(e);
            }
        }
    }
    
    public static void disable() {
        Mods.LoadedMod mod = Vars.mods.getMod(Main.class);
        if (mod != null){
            Core.settings.put("mod-" + mod.name + "-enabled", false);
            mod.state = Mods.ModState.disabled;
            runOnUI(() -> Vars.ui.showInfo("Bootstrapper Disabled"));
        }
    }
  
    private static void downloadLibrary0(Iterator<Map.Entry<String, File>> iterator, boolean yesToAll){
        if (!iterator.hasNext() && !Vars.headless){
            Core.app.post(()->Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit));
            return;
        }
        while (iterator.hasNext()){
            Map.Entry<String, File> s = iterator.next();
            if(s.getValue().exists()){
                continue;
            }
            Seq<URL> seq = Seq.with(downloadList.get(s.getKey()));
            URL url = seq.random();
    
            String size = sizeList.get(s.getKey());
            if(size == null){
                try {//blocking
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    size = SharedBootstrapper.humanReadableByteCountSI(connection.getContentLengthLong());
                    size = "(" + size + ")";
                    connection.disconnect();
                }catch(IOException e){
                    size = "(don't know)";
                }
                sizeList.put(s.getKey(),size);
            }
            Log.info("Downloading: " + s.getKey());
            if(!yesToAll)
            if(Core.settings.getString(s.getKey()) != null)
                continue;
            if(Vars.headless){
                BootstrapperUI.download(seq.random().toExternalForm(), new Fi(s.getValue()), () -> { }, Throwable::printStackTrace);
            }else{
                Runnable run = ()->BootstrapperUI.downloadGUI(url.toExternalForm(), new Fi(s.getValue()), () -> {
                    if(!yesToAll) downloadLibrary0(iterator,yesToAll);
                },()->{
                    if(!yesToAll) downloadLibrary0(iterator,yesToAll);
                });
                String finalSize = size;
             
                
                Core.app.post(()->{
                    if(yesToAll){
                        run.run();
                    }else{
                        Vars.ui.showCustomConfirm("Download Library", s.getKey(), "Download " + (finalSize == null ? "" : finalSize), "Skip", run, () -> {
                            Core.settings.put(s.getKey(), "skip");
                            if(!yesToAll) downloadLibrary0(iterator,yesToAll);
                        });
                    }
                });
           
            }
            if(!yesToAll)break;
        }
    
       
        
    
    }
    public static void downloadLibrary(){
        final Iterator<Map.Entry<String, File>> iterator = new HashMap<>(downloadFile).entrySet().iterator();
        if(Vars.headless){
            Core.app.post(() -> downloadLibrary0(iterator, true));
        }else{
            Main.runOnUI(()->{
                BaseDialog dialog = new BaseDialog("Download Library");
                dialog.cont.add(downloadFile.size() + " library total").width(mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
                dialog.buttons.defaults().size(200f, 54f).pad(2f);
                dialog.setFillParent(false);
                dialog.buttons.button("No", () -> {
                    dialog.hide();
                });
                dialog.buttons.button("Yes", () -> {
                    dialog.hide();
                    Threads.daemon(() -> downloadLibrary0(iterator, false));
                });
                dialog.buttons.button("Yes to all", () -> {
                    dialog.hide();
                    Threads.daemon(() -> downloadLibrary0(iterator, true));
                });
                dialog.keyDown(KeyCode.escape, dialog::hide);
                dialog.keyDown(KeyCode.back, dialog::hide);
                dialog.show();
            });
        }
    }
    public static Fi getFlavorJar(String flavor){
        String path = flavor.replace('-', '/') + ".jar";
        return Core.files.cache(path);
    }
    public static void load() {
        if (System.getProperty("glopion.loaded", "0").equals("1")){
            Log.errTag("Glopion-Bootstrapper", "Trying to load multiple times !!!");
            return;
        }
        System.setProperty("glopion.loaded", "1");
        
      
        jar = getFlavorJar(flavor);
        SharedBootstrapper.parent = Core.files.cache("libs").file();
        
        boolean classExist = Main.class.getClassLoader().getResourceAsStream(classpath.replace('.','/')+".class") != null;
        if(classExist){
            Log.infoTag("Glopion-Bootstrapper", "Found in classpath, loading from classpath");
        }
        if (jar.exists() || classExist){
            Log.infoTag("Glopion-Bootstrapper", "Loading: " + jar.absolutePath());
            try {
                ClassLoader parent = Main.class.getClassLoader();
                Log.info(parent.getClass().getSimpleName());
                classLoader = Vars.platform.loadJar(jar, parent);
                InputStream is = classLoader.getResourceAsStream("dependencies");
                if(is != null){
                    Log.info("found dependencies list");
                    if(Vars.mobile)
                        Log.err("IN MOBILE");
                    checkDependency(is);
                }
                
                if(!Vars.mobile && somethingMissing()){
                    downloadLibrary();
                }
                Seq<URL> urls = new Seq<>();
                    if(downloadFile.size() != 0){
                   
                        for(File s : downloadFile.values()) {
                            if(s.exists())
                                urls.add (s.toURI().toURL());
                        }
                        if(!urls.isEmpty()){
                            urls.add(jar.file().toURI().toURL());
                            URL[] url = new URL[urls.size];
                            int i = 0;
                            for (URL url1 : urls) {
                                url[i++] = url1;
                            }
                            classLoader = new URLClassLoader(url, parent);
                        }
                    }
                    unloaded = (Class<? extends Mod>) Class.forName(classpath, true, classLoader);
                
                StringBuilder sb = new StringBuilder().append("Class: ").append(unloaded).append("\n");
                sb.append("Flavor: ").append(flavor).append("\n");
                sb.append("Classpath: ").append(jar.absolutePath()).append("\n");
                sb.append("Size: ").append(jar.length()).append(" bytes\n");
                sb.append("Classloader: ").append(classLoader.getClass()).append("\n");
                if(dependencies.size() != 0){
                    sb.append("Dependency: ").append("\n");
                    for(URL o : urls){
                        sb.append(" ").append(o).append("\n");
                    }
                }
                info = sb.toString();
            }catch(Throwable e){
                handleException(e);
            }
        }else{
            Log.warn(jar.absolutePath() + " doesn't exist, loading in next startup to prevent game freeze");
            downloadThing = true;
        }
        
        
    }
   
    
    public static void handleException(Throwable e) {
        e.printStackTrace();
        error.add(e);
        Log.errTag("Glopion-Bootstrapper", e.toString());
        runOnUI(() -> Vars.ui.showException("Glopion-Bootstrapper Failed To Load", e));
    
    
    }
    
    public static void runOnUI(Runnable r) {
        if (Vars.ui == null || Vars.ui.loadfrag == null || Core.scene == null){
            Events.on(EventType.ClientLoadEvent.class, cr -> runOnUI(r));
        }else{
            r.run();
        }
    }
    
    @Override
    public void init() {
        bootstrapper.init();
        if (loaded != null) try {
            loaded.init();
        }catch(Exception e){
            handleException(e);
        }
    }
    
}
