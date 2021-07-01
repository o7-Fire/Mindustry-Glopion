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
import mindustry.mod.ModClassLoader;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.BaseDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static mindustry.Vars.mobile;
import static org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper.*;

public class Main extends Mod {
    public static final ArrayList<Throwable> error = new ArrayList<>();
    public static String flavor = Core.settings.getString("glopion-flavor", "Release-" + Version.buildString());
    public static String baseURL = Core.settings.getString("glopion-url", "https://raw.githubusercontent.com/o7-Fire/Mindustry-Glopion/main/");
    public static ClassLoader mainClassloader;
    public static Class<? extends Mod> unloaded = null;
    public static Mod loaded = null;
    public static boolean downloadThing = false;
    public static BootstrapperUI bootstrapper;
    public static Fi jar;
    public static Main main;
    public static String classpath = "org.o7.Fire.Glopion.";
    public static String info = "None";
    
    
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
        if(!Vars.headless)
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
    
    private static void downloadLibrary0(Iterator<Map.Entry<String, File>> iterator, boolean yesToAll) {
        if (!iterator.hasNext() && !Vars.headless){
            Core.app.post(() -> Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit));
            return;
        }
        while (iterator.hasNext()) {
            Map.Entry<String, File> s = iterator.next();
            if (s.getValue().exists()){
                continue;
            }
            Seq<URL> seq = Seq.with(downloadList.get(s.getKey()));
            URL url = seq.random();
            
            String size = sizeList.get(s.getKey());
            if (size == null){
                try {//blocking
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    size = SharedBootstrapper.humanReadableByteCountSI(connection.getContentLengthLong());
                    size = "(" + size + ")";
                    connection.disconnect();
                }catch(IOException e){
                    size = "(don't know)";
                }
                sizeList.put(s.getKey(), size);
            }
            Log.info("Downloading: " + s.getKey());
            if (!yesToAll) if (Core.settings.getString(s.getKey()) != null) continue;
            if (Vars.headless){
                BootstrapperUI.download(seq.random().toExternalForm(), new Fi(s.getValue()), () -> { }, Throwable::printStackTrace);
            }else{
                Runnable run = () -> BootstrapperUI.downloadGUI(url.toExternalForm(), new Fi(s.getValue()), () -> {
                    downloadLibrary0(iterator, yesToAll);
                }, () -> {
                    Core.settings.put(s.getKey(), "skip");
                    downloadLibrary0(iterator, yesToAll);
                });
                String finalSize = size;
                
                
                Core.app.post(() -> {
                    if (yesToAll){
                        run.run();
                    }else{
                        Vars.ui.showCustomConfirm("Download Library", s.getKey(), "Download " + (finalSize == null ? "" : finalSize), "Skip", run, () -> {
                            Core.settings.put(s.getKey(), "skip");
                            downloadLibrary0(iterator, yesToAll);
                        });
                    }
                });
            }
            break;
        }
        
        
    }
    
    public static void forgetSkip(){
        for (Map.Entry<String, File> s : downloadFile.entrySet())
            Core.settings.remove(s.getKey());
    }
    
    public static void downloadLibrary() {
        long totalSize = 0, totalDownload = 0;
        TreeMap<String, File> list = new TreeMap<>();
        for (Map.Entry<String, File> s : downloadFile.entrySet()) {
            if (!s.getValue().exists() && Core.settings.get(s.getKey(), null) == null){
                list.put(s.getKey(), s.getValue());
                Long l = sizeLongList.get(s.getKey());
                if (l != null){
                    totalSize = totalSize + l;
                }
                totalDownload++;
            }
        }
        if(list.size() == 0)return;
        final Iterator<Map.Entry<String, File>> iterator = new HashMap<>(downloadFile).entrySet().iterator();
        if (Vars.headless){
            Core.app.post(() -> downloadLibrary0(iterator, true));
        }else{
            long finalTotalDownload = totalDownload;
            long finalTotalSize = totalSize;
            Main.runOnUI(() -> {
                BaseDialog dialog = new BaseDialog("Download Library");
                dialog.cont.add("Some library may platform dependent, you can skip it\n" + finalTotalDownload + " Library Total\n Size Total: " + SharedBootstrapper.humanReadableByteCountSI(finalTotalSize)).width(mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
                dialog.buttons.defaults().size(200f, 54f).pad(2f);
                dialog.setFillParent(false);
                dialog.buttons.button("Skip to all", () -> {
                    while (iterator.hasNext()) Core.settings.put(iterator.next().getKey(), "skip");
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
    
    public static Fi getFlavorJar(String flavor) {
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
        
        boolean classExist = Main.class.getClassLoader().getResourceAsStream(classpath.replace('.', '/') + ".class") != null;
        if (classExist){
            Log.infoTag("Glopion-Bootstrapper", "Found in classpath, loading from classpath");
        }
        if (jar.exists() || classExist){
            
            Log.infoTag("Glopion-Bootstrapper", "Loading: " + jar.absolutePath());
            //TODO handle development enviroment classpath, URL classpath for dependency,
            Seq<URL> urls = new Seq<>();
            ModClassLoader modClassloader = new ModClassLoader();
            try{
                modClassloader = (ModClassLoader) Vars.mods.mainLoader();
            }catch(ClassCastException ignored){}
            
            ClassLoader //
                    parentClasslaoder = Main.class.getClassLoader(),//if development enviroment then its system else Platform.loadjar
                    platformClassloader = null, //Glopion instance classloader, handled by mindustry
                    dependencyClassloader = null;//Glopion desktop only, override everything when its not development enviroment
            if (classExist) mainClassloader = parentClasslaoder;
            if (!classExist) try {
                
                while (parentClasslaoder.getParent() != null && parentClasslaoder.getClass() != ModClassLoader.class)
                    parentClasslaoder = parentClasslaoder.getParent();
                if (parentClasslaoder instanceof ModClassLoader) modClassloader = (ModClassLoader) parentClasslaoder;
                if(!Vars.android){
                    //forbidden pacakage name "java", wait how bootstrapper (bootstrapper.jar) manage to load
                    platformClassloader = new URLClassLoader(new URL[]{jar.file().toURI().toURL()}, parentClasslaoder);
                }else{
                    //assume core version
                    platformClassloader = Vars.platform.loadJar(jar, parentClasslaoder);
                }
                //if not development enviroment then its must be Vars.mods.mainLoader()
                modClassloader.addChild(platformClassloader);
                //desktop
                InputStream is = platformClassloader.getResourceAsStream("dependencies");
                if (is != null){
                    Log.info("found dependencies list");
                    if (Vars.mobile) Log.err("IN MOBILE");
                    checkDependency(is);
                }
                
                if (!Vars.mobile && somethingMissing()){
                    downloadLibrary();
                }
                mainClassloader = modClassloader;
                
                //assume its desktop
                if (downloadFile.size() != 0){
                    for (File s : downloadFile.values()) {
                        if (s.exists()) urls.add(s.toURI().toURL());
                    }
                    if (!urls.isEmpty()){
                        urls.add(jar.file().toURI().toURL());
                        URL[] url = new URL[urls.size];
                        int i = 0;
                        for (URL url1 : urls) {
                            url[i++] = url1;
                        }
                        dependencyClassloader = new URLClassLoader(url);
                       
                        mainClassloader = dependencyClassloader;
                        //modClassloader.addChild(dependencyClassloader);
                    }
                }
                Log.infoTag("Glopion-Bootstrapper", "Parent: " + parentClasslaoder.getClass().getSimpleName());
            }catch(Throwable e){
                handleException(e);
            }
            
            if (mainClassloader != null) try {
                modClassloader.addChild(mainClassloader);
                Log.infoTag("Glopion-Bootstrapper", "Main: " + mainClassloader.getClass().getSimpleName());
                unloaded = (Class<? extends Mod>) Class.forName(classpath, true, mainClassloader);
            }catch(Throwable e){
                handleException(e);
            }
            
            StringBuilder sb = new StringBuilder().append("Class: ").append(unloaded).append("\n");
            sb.append("Flavor: ").append(flavor).append("\n");
            sb.append("Classpath: ").append(jar.absolutePath()).append("\n");
            sb.append("Size: ").append(jar.length()).append(" bytes\n");
            sb.append("Main Classloader: ").append(mainClassloader).append("\n");
            sb.append("Mod Classloader: ").append(modClassloader).append("\n");
            sb.append("Parent Classloader: ").append(parentClasslaoder).append("\n");
            sb.append("Platform Classloader: ").append(platformClassloader).append("\n");
            sb.append("Dependency Classloader: ").append(dependencyClassloader).append("\n");
            
            if (dependencies.size() != 0){
                sb.append("Dependency: ").append("\n");
                for (URL o : urls) {
                    sb.append(" ").append(o).append("\n");
                }
            }
            info = sb.toString();
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
        if(bootstrapper != null)
            bootstrapper.init();
        if (loaded != null) try {
            loaded.init();
        }catch(Exception e){
            handleException(e);
        }
    }
    
}
