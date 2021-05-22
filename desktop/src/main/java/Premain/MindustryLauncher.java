package Premain;

import Atom.Reflect.Reflect;
import mindustry.desktop.DesktopLauncher;

public class MindustryLauncher {
    public static void main(String[] args) {
        if (System.getProperty("dev") != null) Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
        DesktopLauncher.main(args);
    }
}
