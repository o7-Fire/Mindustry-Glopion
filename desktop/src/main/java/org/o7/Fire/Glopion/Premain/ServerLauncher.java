package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import arc.backend.headless.HeadlessApplication;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.net.Net;

import java.lang.reflect.Field;

import static arc.util.Log.logger;
import static mindustry.Vars.platform;

public class ServerLauncher {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        if (System.getProperty("dev") != null){
            Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
            Reflect.debug = true;
        }
        
        Field fargs = mindustry.server.ServerLauncher.class.getDeclaredField("args");
        fargs.setAccessible(true);
        fargs.set(null,args);
        MindustryLauncher.patchClassloader(new Platform() {});
        Vars.net = new Net(platform.getNet());
        logger = (level1, text) -> {
            System.out.println(text);
        };
        new HeadlessApplication(new mindustry.server.ServerLauncher(), throwable -> throwable.printStackTrace());
    }
}
