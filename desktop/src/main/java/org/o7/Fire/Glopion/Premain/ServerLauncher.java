package org.o7.Fire.Glopion.Premain;

import Atom.Reflect.Reflect;
import arc.ApplicationListener;
import arc.backend.headless.HeadlessApplication;
import arc.func.Cons;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.Platform;
import mindustry.net.Administration;
import mindustry.net.Net;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static arc.util.Log.logger;
import static mindustry.Vars.platform;

public class ServerLauncher extends mindustry.server.ServerLauncher {
    public static final List<Throwable> exception = Collections.synchronizedList(new ArrayList<>());
    public static HeadlessApplicationWithExtraModification application;
    
    
    public static void main(String[] args) {
        if (System.getProperty("dev") != null) {
            Reflect.DEBUG_TYPE = Reflect.DebugType.DevEnvironment;
            Reflect.debug = true;
    
            MindustryLauncher.loadWithoutFile();
        }


        try {
            Field fargs = null;
            fargs = mindustry.server.ServerLauncher.class.getDeclaredField("args");
            fargs.setAccessible(true);
            fargs.set(null, args);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            Log.err("Failed to hack args");
        }

        MindustryLauncher.patchClassloader(new Platform() {
        });
        Vars.net = new Net(platform.getNet());
        logger = (level1, text) -> {
            System.out.println(text);
        };
        application = new HeadlessApplicationWithExtraModification(new ServerLauncher(), t -> {
            if (t instanceof VirtualMachineError) throw (VirtualMachineError) t;
            exception.add(t);
            t.printStackTrace();
        });
    }
    
    
    @Override
    public void init() {
        if (System.getProperty("dev") != null) Administration.Config.debug.set(true);
        super.init();

    }

    public static class HeadlessApplicationWithExtraModification extends HeadlessApplication {

        public HeadlessApplicationWithExtraModification(ApplicationListener listener) {
            super(listener);
        }

        public HeadlessApplicationWithExtraModification(ApplicationListener listener, Cons<Throwable> exceptionHandler) {
            super(listener, exceptionHandler);
        }

        public HeadlessApplicationWithExtraModification(ApplicationListener listener, float renderIntervalSec, Cons<Throwable> exceptionHandler) {
            super(listener, renderIntervalSec, exceptionHandler);
        }

        @Override
        public boolean isAndroid() {
            MindustryLauncher.hijacker();
            return super.isAndroid();
        }

        public boolean alive() {
            return running;
        }
    }
}
