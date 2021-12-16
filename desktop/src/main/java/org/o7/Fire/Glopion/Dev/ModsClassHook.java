package org.o7.Fire.Glopion.Dev;

import Atom.Reflect.Reflect;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.mod.ModClassLoader;
import mindustry.mod.Mods;
import org.o7.Fire.Glopion.Internal.InformationCenter;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModsClassHook {

    Seq<Mods.LoadedMod> mods;
    ObjectMap<Class<?>, Mods.ModMeta> metas;
    Mods loader;
    Fi thisJar;
    Method sortModsMethod;

    public ModsClassHook(Mods loader) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        this.loader = loader;
        this.thisJar = new Fi(InformationCenter.getCurrentJar());
        Field modsField = loader.getClass().getDeclaredField("mods"), metasField = loader.getClass()
                .getDeclaredField("metas");
        modsField.setAccessible(true);
        metasField.setAccessible(true);
        mods = (Seq<Mods.LoadedMod>) modsField.get(loader);
        metas = (ObjectMap<Class<?>, Mods.ModMeta>) metasField.get(loader);
        sortModsMethod = loader.getClass().getDeclaredMethod("sortMods");
        sortModsMethod.setAccessible(true);

    }

    public boolean sortMods() {
        try {
            sortModsMethod.invoke(loader);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    public void load(String className) {
        Mods.ModMeta meta = new Mods.ModMeta();
        meta.name = className;
        meta.author = "Glopion";
        meta.description = Reflect.getCallerClassStackTrace().toString();
        meta.version = "1.0";
        meta.hidden = true;
        meta.java = true;

        load(className, meta, thisJar);
    }

    public void load(String className, Mods.ModMeta meta, Fi jar) {
        try {
            ClassLoader classLoader = Vars.platform.loadJar(jar, loader.mainLoader());
            if (loader.mainLoader() instanceof ModClassLoader) {
                ((ModClassLoader) loader.mainLoader()).addChild(classLoader);
            }
            Class<?> clazz = Class.forName(className, true, classLoader);
            metas.put(clazz, meta);
            Mod mainMod = (Mod) clazz.getDeclaredConstructor().newInstance();
            Fi classFile = new Fi(new File(mainMod.getClass().getProtectionDomain().getCodeSource().getLocation()
                    .toURI()));
            Mods.LoadedMod mod = new Mods.LoadedMod(jar, classFile, mainMod, classLoader, meta);
            mods.add(mod);
            sortMods();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
