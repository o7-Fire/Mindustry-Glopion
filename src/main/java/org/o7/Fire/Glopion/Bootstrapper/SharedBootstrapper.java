package org.o7.Fire.Glopion.Bootstrapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        if(parent == null)throw new NullPointerException("Cache File is Null");
        downloadFile.clear();
        downloadList.clear();
        dependencies.clear();
        dependencies.load(is);
        if(dependencies.size() == 0)return;
        SharedBootstrapper.parent.mkdirs();
        for (String key : dependencies.stringPropertyNames()){
            String[] download = dependencies.getProperty(key).split(" ");
            ArrayList<URL> downloadURL = new ArrayList<>();
            for(String s : download)
                downloadURL.add(new URL(s));
            downloadList.put(key, downloadURL);
            File downloadPath = new File(parent, key.replace(':',File.separatorChar)).getAbsoluteFile();
            downloadPath.getParentFile().mkdirs();
            downloadFile.put(key,downloadPath);
        }
    }
    public static boolean somethingMissing(){
        for (Map.Entry<String, File> download : downloadFile.entrySet()){
            if(!download.getValue().exists())return true;
        }
        return false;
    }
    public static void downloadAll(){
        for (Map.Entry<String, File> download : downloadFile.entrySet()){
            System.out.println("Downloading: " + download.getKey());
            for(URL url : downloadList.get(download.getKey())) {
                if(download.getValue().exists())continue;
                FileOutputStream fos = null;
                System.out.println("Downloading From: " + url);
                try {
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    fos = new FileOutputStream(download.getValue());
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }catch(IOException e){
                    e.printStackTrace();
                   
                }finally{
                    if(fos != null){
                        try {
                            fos.close();
                        }catch(IOException ignored){
            
                        }
                    }
                }
            }
        }
    }
    
}
