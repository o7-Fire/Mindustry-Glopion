//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mindustry.mod;

import Atom.Bootstrap.AtomicBootstrap;
import arc.struct.Seq;

import java.net.MalformedURLException;
//help override class in Mindustry/mods by intellij /out/ (Ant Build) to bypass gradle build system (damn you gradle)
public class ModClassLoader extends ClassLoader {
    private Seq<ClassLoader> children = new Seq();
    private ThreadLocal<Boolean> inChild = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private AtomicBootstrap systemClassloader = new AtomicBootstrap(this.getClass().getClassLoader());
    public ModClassLoader() {
        try {
            systemClassloader.loadClasspath();
            systemClassloader.loadCurrentClasspath();
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        
    }
    
    public void addChild(ClassLoader child) {
        this.children.add(child);
    }
    
    /** Try find in system classpath, if not exist back to jar **/
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try{
           return systemClassloader.atomClassLoader.loadClass(name);
        }catch(Throwable ignored){}
        if (this.inChild.get()) {
            this.inChild.set(false);
            throw new ClassNotFoundException(name);
        } else {
            ClassNotFoundException last = null;
            int size = this.children.size;
            int i = 0;
            
            while(i < size) {
                try {
                    Class var5;
                    try {
                        this.inChild.set(true);
                        var5 = ((ClassLoader)this.children.get(i)).loadClass(name);
                    } finally {
                        this.inChild.set(false);
                    }
                    
                    return var5;
                } catch (ClassNotFoundException var10) {
                    last = var10;
                    ++i;
                }
            }
            
            throw last == null ? new ClassNotFoundException(name) : last;
        }
    }
}
