package org.o7.Fire.Glopion.Bootstrapper;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.input.KeyCode;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Mods;
import mindustry.mod.Plugin;
import mindustry.ui.dialogs.BaseDialog;
import org.o7.Fire.Glopion.Bootstrapper.UI.FlavorDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static mindustry.Vars.mobile;
import static mindustry.Vars.ui;
import static org.o7.Fire.Glopion.Bootstrapper.SharedBootstrapper.*;

public class Main extends Plugin {
    public static final List<Throwable> error = new ArrayList<>();
    public static String flavor = Core.settings.getString("glopion-flavor", "Release-" + Version.buildString());
    public static String baseURL = Core.settings.getString("glopion-url", "https://raw.githubusercontent.com/o7-Fire/Mindustry-Glopion/main/");
    public static ClassLoader mainClassloader;
    public static Class<? extends Mod> unloaded = null;
    public static Mod loaded = null;
    public static boolean downloadThing = false;
    public static BootstrapperUI bootstrapper;
    public static Fi jar;
    public static String GlopionBootstrapperText = "Glopion-Bootstrapper";
    public static String classpath = "org.o7.Fire.Glopion.";
    public static String info = "None";
    public static String glopionLoadedString = "glopion.loaded";
    public static Properties release = new Properties();

    static {
        classpath = classpath + flavor.split("-")[0] + "Launcher";
        if (!Vars.headless) bootstrapper = new BootstrapperUI();
    }
    
    public Main() {
        if (System.getProperty(glopionLoadedString, "0").equals("1")){
            Log.errTag(GlopionBootstrapperText, "Trying to load multiple times !!!");
            Log.err(new RuntimeException("Trying to load Glopion multiple times !!!"));
            try {
                Log.errTag("Glopion-Location", Main.class.getProtectionDomain().getCodeSource().getLocation().toString());
            }catch(Exception ignored){
            
            }
            return;
        }
        
        Log.infoTag("Mindustry-Version", Version.buildString());
        Log.infoTag("Mindustry-Version-Combined", Version.combined());
        Log.infoTag(GlopionBootstrapperText, "Flavor: " + flavor);
        Log.infoTag(GlopionBootstrapperText, "Classpath: " + classpath);
        Log.infoTag("Platform", SharedBootstrapper.getPlatform());
        try {
            load();
        }catch(Throwable t){
            handleException(t);
        }
        
        if (unloaded != null){
            try {
                loaded = unloaded.getDeclaredConstructor().newInstance();
            }catch(Throwable e){
                handleException(e);
            }
        }
    }
    
    private static void downloadLibrary0(Iterator<Map.Entry<String, File>> iterator, boolean yesToAll) {
        if (!iterator.hasNext() ){
            Core.app.post(() -> {
                if(Vars.ui != null)
                    Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
                else
                    Log.infoTag("Downloader","Finished Downloading");
            });
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
                    size = SharedBootstrapper.humanReadableByteCountSI(Long.parseLong(connection.getHeaderField("content-length")));
                    size = "(" + size + ")";
                    connection.disconnect();
                }catch(IOException e){
                    size = "(don't know)";
                }
                sizeList.put(s.getKey(), size);
            }
            Log.info("Downloading: " + s.getKey());
            if (!yesToAll && Core.settings.getString(s.getKey()) != null) continue;
            if (Vars.headless || test){
                Log.infoTag("Downloader", s.getKey() + " " + size);
                try {
                    SharedBootstrapper.download(seq.random(), s.getValue()).join();
                }catch(InterruptedException e){
                    Log.err(e);
                }
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
            if (!(Vars.headless || test)) break;
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
    
    public static void downloadGlopionNow(String flavor) throws IOException, InterruptedException {
        Log.info("Downloading now");
        fetchRelease(baseURL);
        if (release.getProperty(flavor) == null) flavor = getFlavorThatExist();
        Log.info("Flavor: " + flavor);
        String path = flavor.replace('-', '/') + ".jar";
        String url = release.getProperty(flavor);
        jar = Core.files.cache(path);
        SharedBootstrapper.download(new URL(url), jar.file()).join();
        classpath = "org.o7.Fire.Glopion." + flavor.split("-")[0] + "Launcher";
    }
    
    public static void load() {
        if (System.getProperty(glopionLoadedString, "0").equals("1")) {
            Log.errTag(GlopionBootstrapperText, "Trying to load multiple times !!!");
            return;
        }
        System.setProperty(glopionLoadedString, "1");
        jar = getFlavorJar(flavor);
        Log.infoTag(GlopionBootstrapperText, "Finding Jar: " + jar.absolutePath());
        if (SharedBootstrapper.localGlopion() != null) {
            jar = new Fi(SharedBootstrapper.localGlopion());
            Log.infoTag(GlopionBootstrapperText, "Found local jar: " + jar.absolutePath());
        }
        SharedBootstrapper.parent = Core.files.cache("libs").file();
        boolean classExist = false;
        try {
            classExist =
                    Main.class.getClassLoader().getResourceAsStream(classpath.replace('.', '/') + ".class") != null;
        } catch (Exception ignored) {

        }
        if (classExist) {
            Log.infoTag(GlopionBootstrapperText, "Found in classpath, loading from classpath");
            InputStream is = Main.class.getClassLoader().getResourceAsStream("dependencies");
            if (is != null){
                Log.info("found dependencies list");
                if (Vars.mobile) Log.err("IN MOBILE");
                try {
                    checkDependency(is);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        if (jar.exists() || classExist || test){
            Log.infoTag(GlopionBootstrapperText, "Loading: " + jar.absolutePath());
        
            Seq<URL> urls = new Seq<>();
            try {
                modClassloader = (ModClassLoader) Vars.mods.mainLoader();
            }catch(ClassCastException ignored){}
            if (classExist) mainClassloader = parentClasslaoder;
            if (!classExist) try {
                if ((test || Vars.headless) && !jar.exists()) {
                    downloadGlopionNow(flavor);
                }
                if (!mobile) {
                    while (parentClasslaoder.getParent() != null &&
                            parentClasslaoder.getClass() != ModClassLoader.class)
                        parentClasslaoder = parentClasslaoder.getParent();
                }
                if (parentClasslaoder instanceof ModClassLoader)
                    modClassloader = (ModClassLoader) parentClasslaoder;
                if (!Vars.android) {
                    //forbidden package name "java", wait how bootstrapper (bootstrapper.jar) manage to load
                    platformClassloader = new URLClassLoader(new URL[]{jar.file().toURI().toURL()}, parentClasslaoder);
                } else {
                    //assume core version
                    platformClassloader = Vars.platform.loadJar(jar, parentClasslaoder);
                }
                //if not development environment then its must be Vars.mods.mainLoader()
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
                    if (!urls.isEmpty()) {
                        urls.add(jar.file().toURI().toURL());
                        URL[] url = new URL[urls.size];
                        int i = 0;
                        for (URL url1 : urls) {
                            url[i++] = url1;
                        }
                        Log.infoTag(GlopionBootstrapperText, "Found " + urls.size + " dependencies to be injected");
                        dependencyClassloader = new URLClassLoader(url, parentClasslaoder);

                        mainClassloader = dependencyClassloader;
                        //modClassloader.addChild(dependencyClassloader);
                    }
                }
                Log.infoTag(GlopionBootstrapperText, "Parent: " + parentClasslaoder.getClass().getSimpleName());
            } catch (Throwable e) {
                handleException(e);
            }

            if (mainClassloader != null) {
                try {
                    modClassloader.addChild(mainClassloader);
                    Log.infoTag(GlopionBootstrapperText, "Main: " + mainClassloader.getClass().getSimpleName());
                    unloaded = (Class<? extends Mod>) Class.forName(classpath, true, mainClassloader);
                } catch (ClassNotFoundException e) {
                    handleException(new ClassNotFoundException(
                            e.getMessage() + " not found, please download dependency"));
                } catch (Throwable e) {
                    handleException(e);
                }
            }
            boolean atomExist = false;
            Throwable whyAtomDontExist = null;
            try {
                Class.forName("Atom.Manifest", false, Main.class.getClassLoader());
                atomExist = true;
            } catch (Throwable e) {
                whyAtomDontExist = e;
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
            sb.append("Atom Library: ").append(atomExist ? "Yes" : whyAtomDontExist.getMessage()).append("\n");
            if (urls.size != 0){
                sb.append("Dependency: ").append("\n");
                for (URL o : urls) {
                    sb.append(" ").append(o).append("\n");
                }
            }
            info = sb.toString();
            if (Vars.headless) Log.debug(info);
        }else{
            Log.warn(jar.absolutePath() + " doesn't exist, loading in next startup to prevent game freeze");
            downloadThing = true;
            jar.file().getAbsoluteFile().mkdirs();
        }
        
        
    }
    
    public static void forgetSkip() {
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
        if (list.size() == 0) return;
        String info = "Some library may platform dependent, you can skip it\n" + totalDownload + " Library Total\n Size Total: " + SharedBootstrapper.humanReadableByteCountSI(totalSize);
        final Iterator<Map.Entry<String, File>> iterator = new HashMap<>(downloadFile).entrySet().iterator();
        if (Vars.headless || test){
            Log.infoTag("Dependency-Downloader", info);
            downloadLibrary0(iterator, true);
        }else{
            long finalTotalDownload = totalDownload;
            long finalTotalSize = totalSize;
            Main.runOnUI(() -> {
                BaseDialog dialog = new BaseDialog("Download Library");
                dialog.cont.add(info).width(mobile ? 400f : 500f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
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
    
    public static ClassLoader //
            parentClasslaoder = Main.class.getClassLoader(),//if development enviroment then its system else Platform.loadjar
            platformClassloader = null, //Glopion instance classloader, handled by mindustry
            dependencyClassloader = null;//Glopion desktop only, override everything when its not development enviroment
    public static ModClassLoader modClassloader = new ModClassLoader(parentClasslaoder);
    
    public static void handleException(Throwable e) {
        if (e instanceof VirtualMachineError) throw ((VirtualMachineError) e);
        e.printStackTrace();
        error.add(e);
        Log.errTag(GlopionBootstrapperText, e.toString());
        runOnUI(() -> Vars.ui.showException("Glopion-Bootstrapper Failed To Load", e));
        
        
    }
    
    public static void onReleaseFetched(InputStream is) throws IOException {
        release.clear();
        release.load(is);
        
        if (release.getProperty(flavor) == null){
            Log.warn("@ Flavor doesn't exist ", flavor);
            runOnUI(() -> ui.showInfo(flavor + " Flavor doesn't exist"));
            return;
        }else{
            runOnUI(() -> ui.showInfoFade("Fetched: [green]" + release.size() + " [white]Flavor"));
        }
    }
    
    public static void fetchRelease(String baseURL) throws IOException {
        baseURL = baseURL.endsWith("/") ? baseURL : baseURL + "/";
        URL u = new URL(baseURL + "release.properties");
        onReleaseFetched(u.openConnection().getInputStream());
    }
    
    public static void fetchRelease(Runnable succ) {
        Thread t = new Thread(() -> {
            try {
                fetchRelease(baseURL);
                succ.run();
            }catch(IOException e){
                handleException(e);
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    public static String getFlavorThatExist() {
        if (release.getProperty(flavor) != null) return flavor;
        Seq<Seq<String>> seq = new Seq<>();
        for (Object s : release.keySet()) {
            try {
                seq.add(FlavorDialog.toKey(String.valueOf(s)));
            }catch(IllegalArgumentException h){}
        }
        seq.sort(FlavorDialog.flavorSort);
        return seq.get(seq.size - 1).toString("-");
    }
    
    public static void tryDownload(String url, String flavor) {
        if (url == null) return;
        String path = flavor.replace('-', '/') + ".jar";
        jar = Core.files.cache(path);
        if (!jar.exists() || Core.settings.getBool("glopion-auto-update", false)){
            Log.infoTag("Glopion-Bootstrapper", "");
            Log.infoTag("Glopion-Bootstrapper", "Downloading: " + url);
            boolean b = !Core.settings.getBoolOnce("glopion-prompt-" + flavor) || !jar.exists();
            if (!Vars.headless && b){
                //sometime jar already exist
                Main.runOnUI(() -> BootstrapperUI.downloadConfirm(url, jar, () -> {
                    if (Main.jar.exists()){
                        Vars.ui.showConfirm("Exit", "Finished downloading do you want to exit", Core.app::exit);
                    }else{
                        ui.showErrorMessage(jar.absolutePath() + " still doesn't exist ??? how");
                    }
                    
                }));
            }else{
                long size = jar.length();
                
                BootstrapperUI.download(url, Main.jar, () -> {
                    if (downloadThing || size != jar.length())
                        runOnUI(() -> ui.showInfoFade(url + " has been downloaded"));
                }, Main::handleException);
            }
        }
    }
    
    public static void tryDownload() {
        tryDownload(release.getProperty(flavor), flavor);
    }
    
    public static void downloadIfNotExist() {
        String url = release.getProperty(flavor);
        if (url == null) return;
        jar = Core.files.cache(flavor.replace('-', '/') + ".jar");
        if (!jar.exists()) tryDownload();
    }
    
    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("glopion-bootstrapper-info", "Get Bootstrapper Information", s -> {
            Log.info(info);
        });
        
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
        }catch(Throwable e){
            handleException(e);
        }
    }
    
}
