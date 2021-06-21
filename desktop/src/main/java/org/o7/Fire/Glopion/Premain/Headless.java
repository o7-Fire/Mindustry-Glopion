package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import mindustry.mod.ModClassLoader;

import java.util.Arrays;
import java.util.List;

public class Headless {
    //run configuration
    //Classpath: Mindustry-Glopion.desktop.test
    //Class org.o7.Fire.Glopion.Premain.Run
    //JVM: 16
    public static void main(String[] args) {
        if(Reflect.debug){
            System.out.println("Mindustry Jar Classloader: " + MindustryLauncher.class.getClassLoader().getClass().getCanonicalName());
            System.out.println("Current Jar Classloader: " + ModClassLoader.class.getClassLoader().getClass().getCanonicalName());
        }
        System.out.println("Args: " + Arrays.toString(args));
        List<String> arg = Arrays.asList(args);
        if(arg.contains("training")){
            System.out.println("training ?");
            
        }
    }
}
