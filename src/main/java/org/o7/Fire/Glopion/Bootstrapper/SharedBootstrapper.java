package org.o7.Fire.Glopion.Bootstrapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

//Java 8+ only
public class SharedBootstrapper {
    public static final long version = 11;
    
    @NotNull
    public static File parent = null;
    public static Properties dependencies = new Properties();
    public static HashMap<String, List<URL>> downloadList = new HashMap<>();
    public static HashMap<String, File> downloadFile = new HashMap<>();
    
    public static void checkDependency(InputStream is) throws IOException {
        downloadFile.clear();
        downloadList.clear();
        dependencies.clear();
        dependencies.load(is);
        if (dependencies.size() == 0) return;
        SharedBootstrapper.parent.mkdirs();
        for (String key : dependencies.stringPropertyNames()) {
            String[] download = dependencies.getProperty(key).split(" ");
            ArrayList<URL> downloadURL = new ArrayList<>();
            for (String s : download)
                downloadURL.add(new URL(s));
            downloadList.put(key, downloadURL);
            File downloadPath = new File(parent, key.replace(':', File.separatorChar)).getAbsoluteFile();
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
       if(System.getProperty("BEVersion", null) == null)
           System.setProperty("BEVersion", "21271");
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
    
    public static void download(URL url, File download) throws IOException {
        FileOutputStream fos = null;
        try {
            download.getAbsoluteFile().getParentFile().mkdirs();
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            fos = new FileOutputStream(download);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }finally{
            try {
                fos.close();
            }catch(Throwable ignored){
                
            }
        }
    }
    public static Collection<File> getFiles(){
        return downloadFile.values();
    }
    public static void downloadAll() {
        for (Map.Entry<String, File> download : downloadFile.entrySet()) {
            System.out.println("Downloading: " + download.getKey());
            for (URL url : downloadList.get(download.getKey())) {
                if (download.getValue().exists()) continue;
                
                System.out.println("Downloading From: " + url);
                try {
                    download(url, download.getValue());
                }catch(IOException e){
                    e.printStackTrace();
                    
                }
            }
        }
    }
    
}
