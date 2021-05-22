package org.o7.Fire.Glopion.Internal;

import Atom.Reflect.Reflect;

import java.io.File;

public class InformationCenter {
    public static File getCurrentJar() {
        try {
            File f = Reflect.getCurrentJar(InformationCenter.class);
            if (f == null){
                f = new File(InformationCenter.class.getClassLoader().getResource(InformationCenter.class.getCanonicalName().replace('.', '/') + ".class").getFile());
            }
            return f;
        }catch(Exception ignored){
            return null;
        }
    }
}
