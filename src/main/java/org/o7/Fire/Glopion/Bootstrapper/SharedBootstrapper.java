package org.o7.Fire.Glopion.Bootstrapper;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

//Java 8+ only
public class SharedBootstrapper {
    public static final long version = 15;
    
    @NotNull
    public static File parent = new File("cache/");
    public static Properties dependencies = new Properties();
    public static HashMap<String, List<URL>> downloadList = new HashMap<>();
    public static TreeMap<String, File> downloadFile = new TreeMap<>();
    public static HashMap<String, String> sizeList = new HashMap<>();
    public static HashMap<String, Long> sizeLongList = new HashMap<>();
 
    public static void checkDependency(InputStream is) throws IOException {
        downloadFile.clear();
        downloadList.clear();
        dependencies.clear();
        dependencies.load(is);
        if (dependencies.size() == 0) return;
        SharedBootstrapper.parent.mkdirs();
        for (String key : dependencies.stringPropertyNames()) {
            String[] download = dependencies.getProperty(key).split(" ");
            String[] keys = key.split("-", 2);
            try {
               
                String size = humanReadableByteCountSI(Long.parseLong(keys[0]));
                key = keys[1];
                sizeList.put(key, size);
                sizeLongList.put(key,Long.parseLong(keys[0]));
             
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
            return release.replace("VERSION",version);
        }
    
        public String getBE(String version) {
            return BE.replace("VERSION",version);
        }
    }
   static {
       if(System.getProperty("MindustryVersion", null) == null)
           System.setProperty("MindustryVersion", "v127.1");
       
   }
    public static URL getMindustryURL() throws MalformedURLException {
        
        return getMindustryURL(MindustryType.Desktop);
    }
    public static File getMindustryFile(MindustryType type) throws MalformedURLException {
        return new File(getMindustryURL(type).getFile().substring(1));
    }
    public static URL getMindustryURL(MindustryType type) throws MalformedURLException {
        if(System.getProperty("BEVersion", null) != null) {
            String h = System.getProperty("BEVersion");
            return new URL(type.getBE(h));
        }
        return new URL(type.getRelease(System.getProperty("MindustryVersion")));
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
        while (!threads.isEmpty()){
            try {
                Thread t = threads.remove(0);
                System.out.println("Waiting: " + t.getName());
                t.join();
            }catch(Throwable e){
                e.printStackTrace();
            }
        }
    }
    public static Thread download(URL url, File download) {
        Thread t = new Thread( ()->{
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
            }finally{
                try {
                    if(closeable != null)
                    closeable.close();
                }catch(Throwable ignored){
            
                }
            }
        }, url.toString());
        t.start();
        return t;
   
    }
    public static Collection<File> getFiles(){
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
