package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import mindustry.desktop.DesktopLauncher;

public class MindustryLauncher {
    public static void main(String[] args) {
        if (System.getProperty("dev") != null){
            Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
            Reflect.debug = true;
        }
        DesktopLauncher.main(args);
    }
}
