package org.o7.Fire.Glopion.Premain;

import java.net.URL;
import java.net.URLClassLoader;

public class DIWHYClassloader extends URLClassLoader {
    public DIWHYClassloader(){
        super("DIWHY", new URL[]{}, null);
    }
    
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if(name.equals(getClass().getName()))return DIWHYClassloader.class;//what
        return super.loadClass(name, resolve);
    }
    
    @Override
    protected Class<?> findClass(String moduleName, String name) {
        return super.findClass(moduleName, name);
    }
}
