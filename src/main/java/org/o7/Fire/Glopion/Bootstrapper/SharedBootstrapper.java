package org.o7.Fire.Glopion.Bootstrapper;

import arc.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

//Java 8 only, no external
public class SharedBootstrapper {
    public static final long version = 34;
    public final static String javaPath;
    public static final boolean test = System.getProperty("test") != null;
    public static final ExecutorService executors = Executors.newCachedThreadPool(r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setName(t.getName() + "-Cached-Pool");
        t.setDaemon(true);
        return t;
    });
    
    static {
        String javaPath1;
        File javaBin = new File(System.getProperty("java.home") + "/bin/java" +
                (System.getProperty("os.name").contains("Windows") ? ".exe" : ""));//smh windows
        javaPath1 = "java";
        if (javaBin.exists()) javaPath1 = javaBin.getAbsolutePath();
    
        javaPath = javaPath1;
        Java.loadConfig();
        if (System.getProperty("MindustryVersion", null) == null)
            System.setProperty("MindustryVersion", Java.config.getProperty("MindustryVersion"));
        if (test) Log.infoTag("Glopion-Boostrapper", "Test Environment");
    }
    
    @NotNull
    public static File parent = new File("cache/");
    public static Properties dependencies = new Properties();
    // name -> download url
    public static HashMap<String, List<URL>> downloadList = new HashMap<>();
    // name -> download file
    public static TreeMap<String, File> downloadFile = new TreeMap<>();
    public static HashMap<String, String> sizeList = new HashMap<>();
    public static HashMap<String, Long> sizeLongList = new HashMap<>();
    public static long totalSize = 0;
    
    public static void addDependency(String name, String url) throws MalformedURLException {
        URL u = new URL(url);
        downloadList.put(name, Collections.singletonList(u));
        downloadFile.put(name, new File(parent, u.getFile()));
        httpGet(url, c -> {
            setReadableSize(name, c.getContentLengthLong());
        }, c -> {});
    }
    
    public static void setReadableSize(String key, long l) {
        String size = humanReadableByteCountSI(l);
        sizeList.put(key, size);
        sizeLongList.put(key, l);
    }
    
    public static void checkDependency(InputStream is) throws IOException {
        downloadFile.clear();
        downloadList.clear();
        dependencies.clear();
        dependencies.load(is);
        if (dependencies.size() == 0) return;
        SharedBootstrapper.parent.mkdirs();
        for (Object objKey : dependencies.keySet()) {
            String key = String.valueOf(objKey);
            String[] download = dependencies.getProperty(key).split(" ");
            String[] keyPlatform = key.split(":");
            if (keyPlatform.length == 4){
                if (!keyPlatform[3].startsWith(getPlatform())){
                    Log.debug("Discarding @, incompatible platform", key);
                    continue;
                }
            }
            //System.out.println(Arrays.toString(keyPlatform));
            String[] keys = key.split("-", 2);
            try {
               long l = Long.parseLong(keys[0]);
               totalSize += l;
    
                key = keys[1];
                String size = humanReadableByteCountSI(l);
                sizeList.put(key, size);
                sizeLongList.put(key, l);
             
            }catch(Exception ignored){}
            ArrayList<URL> downloadURL = new ArrayList<>();
            for (String s : download)
                downloadURL.add(new URL(s));
            downloadList.put(key, downloadURL);
            File downloadPath = new File(parent, downloadURL.get(0).getFile());
            downloadPath.getParentFile().mkdirs();
            downloadFile.put(key, downloadPath);
        }
    }
    public enum MindustryType{
        Desktop("https://github.com/Anuken/Mindustry/releases/download/VERSION/Mindustry.jar",
                "https://github.com/Anuken/MindustryBuilds/releases/download/VERSION/Mindustry-BE-Desktop-VERSION.jar"),
        Android("No-There-is-None",
                "https://github.com/Anuken/MindustryBuilds/releases/download/VERSION/Mindustry-BE-Android-VERSION.apk"),
        Server("https://github.com/Anuken/Mindustry/releases/download/VERSION/server-release.jar",
                "https://github.com/Anuken/MindustryBuilds/releases/download/VERSION/Mindustry-BE-Server-VERSION.jar");
        String release, BE;
        MindustryType(String release, String BE){
            this.release = release;
            this.BE = BE;
        }
    
        public String getRelease(String version) {
            return release.replace("VERSION", version);
        }
    
        public String getBE(String version) {
            return BE.replace("VERSION", version);
        }
    }
    
    public static void httpGet(String url, Consumer<HttpURLConnection> connected, Consumer<Exception> died) {
        executors.submit(() -> {
            HttpURLConnection connection = null;
            try {
                URL u = new URL(url);
                connection = (HttpURLConnection) u.openConnection();
                connected.accept(connection);
            }catch(Exception e){
                died.accept(e);
            }finally{
                if (connection != null){
                    connection.disconnect();
                }
            }
        });
    }
    
    public static String getPlatform() {
        String jvmName = System.getProperty("java.vm.name", "").toLowerCase();
        String osName = System.getProperty("os.name", "").toLowerCase();
        String osArch = System.getProperty("os.arch", "").toLowerCase();
        String abiType = System.getProperty("sun.arch.abi", "").toLowerCase();
        String libPath = System.getProperty("sun.boot.library.path", "").toLowerCase();
        if (jvmName.startsWith("dalvik") && osName.startsWith("linux")){
            osName = "android";
        }else if (jvmName.startsWith("robovm") && osName.startsWith("darwin")){
            osName = "ios";
            osArch = "arm";
        }else if (osName.startsWith("mac os x") || osName.startsWith("darwin")){
            osName = "macosx";
        }else{
            int spaceIndex = osName.indexOf(' ');
            if (spaceIndex > 0){
                osName = osName.substring(0, spaceIndex);
            }
        }
        if (osArch.equals("i386") || osArch.equals("i486") || osArch.equals("i586") || osArch.equals("i686")){
            osArch = "x86";
        }else if (osArch.equals("amd64") || osArch.equals("x86-64") || osArch.equals("x64")){
            osArch = "x86_64";
        }else if (osArch.startsWith("aarch64") || osArch.startsWith("armv8") || osArch.startsWith("arm64")){
            osArch = "arm64";
        }else if ((osArch.startsWith("arm")) && ((abiType.equals("gnueabihf")) || (libPath.contains("openjdk-armhf")))){
            osArch = "armhf";
        }else if (osArch.startsWith("arm")){
            osArch = "arm";
        }
        return osName + "-" + osArch;
    }

    public static URL getMindustryURL() {

        return getMindustryURL(MindustryType.Desktop);
    }

    public static File getMindustryFile(MindustryType type) {
        return new File(getMindustryURL(type).getFile().substring(1));
    }

    public static URL getMindustryURL(MindustryType type) {
        try {
            if (System.getProperty("BEVersion", null) != null) {
                String h = System.getProperty("BEVersion");
                return new URL(type.getBE(h));
            }
            return new URL(type.getRelease(System.getProperty("MindustryVersion")));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean somethingMissing() {
        for (Map.Entry<String, File> download : downloadFile.entrySet()) {
            if (!download.getValue().exists()) return true;
        }
        return false;
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
    public static void waitForThreads(List<Thread> threads){
        while (!threads.isEmpty()) {
            try {
                Thread t = threads.remove(0);
                System.out.println("Waiting: " + t.getName());
                t.join();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static Thread download(URL url, File download) {
        return download(url, download, Throwable::printStackTrace);
    }

    public static Thread download(URL url, File download, Consumer<Throwable> died) {
        Thread t = new Thread(() -> {
            Closeable closeable = null;
            try {

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedInputStream in = new BufferedInputStream(con.getInputStream());
                download.getAbsoluteFile().getParentFile().mkdirs();
                RandomAccessFile randomAccessFile = new RandomAccessFile(download, "rw");
                closeable = randomAccessFile;
                byte[] data = new byte[4096];
                int x;
                while((x = in.read(data, 0, data.length)) >= 0) {
                    randomAccessFile.write(data, 0, x);
                }
                randomAccessFile.close();
            }catch(IOException e){
                e.printStackTrace();
            } finally {
                try {
                    if (closeable != null)
                        closeable.close();
                } catch (Throwable ignored) {

                }
            }
        }, url.toString());
        t.setUncaughtExceptionHandler((t1, e) -> {
            if (died != null)
                died.accept(e);
        });
        t.start();
        return t;

    }

    public static File localGlopion() {

        File f;
        f = new File("Mindustry-Glopion-DeepPatch.jar");
        if (f.exists()) return f;
        f = new File("Mindustry-Glopion-Core.jar");
        if (f.exists()) return f;
        return null;

    }

    public static Collection<File> getFiles() {
        return downloadFile.values();
    }

    public static void downloadAll() {
        ArrayList<Thread> threads = new ArrayList<>();
        for (Map.Entry<String, File> download : downloadFile.entrySet()) {
            System.out.println("Downloading: " + download.getKey());
            for (URL url : downloadList.get(download.getKey())) {
                if (download.getValue().exists()) continue;
                
                System.out.println("Downloading From: " + url);
                threads.add( download(url, download.getValue()));
              
            }
        }
       waitForThreads(threads);
    }
    
}
