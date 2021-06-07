package org.o7.Fire.Glopion.Module;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Mods;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.Patch.UIPatch;
import org.o7.Fire.Glopion.Module.Patch.VarsPatch;
import org.o7.Fire.Glopion.Patch.EventHooker;
import org.o7.Fire.Glopion.Patch.Translation;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Handle all module creation and initialization, only invoked once
 */
public class ModuleRegisterer implements Module {
    public static HashMap<Class<? extends ModsModule>, ModsModule> modules = new HashMap<>();
    private static HashSet<Class<? extends ModsModule>> unloadedModules = new HashSet<>();
    private volatile static boolean init, preInit, postInit;
    
    public static <T> void invokeAllAs(Class<T> tClass, Consumer<T> consumer) {
        invokeAll(module -> {
            if (tClass.isInstance(module)){
                consumer.accept((T) module);
            }
        });
    }
    
    public static void invokeAll(Consumer<ModsModule> m) {
        for (ModsModule value : modules.values()) {
            m.accept(value);
        }
    }
    
    public static ModsModule registerModule(Class<? extends ModsModule> c) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (modules.containsKey(c)) return modules.get(c);
        return c.getDeclaredConstructor().newInstance();
    }
    
    public void core() {
        unloadedModules.addAll(Arrays.asList(Translation.class, VarsPatch.class, UIPatch.class, EventHooker.class));
        if (!Vars.mobile){
            unloadedModules.addAll(InformationCenter.getExtendedClass(ModsModule.class));
        }
    }
    
    public void preInit() {
        if (preInit) throw new RuntimeException("PreInit Already Loaded");
        preInit = true;
        HashSet<Class<? extends ModsModule>> loadedModules = new HashSet<>();
        Fi currentJar = new Fi(InformationCenter.getCurrentJar());
        Fi root = currentJar.parent();
        ModsModule stub = new ModsModule() {};
        int count = 0;
        while (!unloadedModules.isEmpty()) {
            Class<? extends ModsModule> unloaded = unloadedModules.iterator().next();
            unloadedModules.remove(unloaded);
            if (modules.containsKey(unloaded)) continue;
            count++;
            loadedModules.add(unloaded);
            try {
                boolean enabled = Core.settings.getBool("mod-" + unloaded.getCanonicalName() + "-enabled", true);
                ModsModule module = stub;
                Mods.ModMeta meta = new Mods.ModMeta();
                if (enabled){
                    module = registerModule(unloaded);
                    modules.put(unloaded, module);
                    ArrayList<Class<? extends ModsModule>> depend = module.dependency;
                    for (Class<? extends ModsModule> h : depend) {
                        meta.dependencies.add(h.getCanonicalName().toLowerCase(Locale.ROOT).replace(" ", "-"));
                        if (modules.containsKey(h)) continue;
                        loadedModules.add(h);
                        unloadedModules.add(h);
                    }
                    
                }else{
                    meta.dependencies.add("¯\\_(ツ)_/¯");
                }
                meta.name = unloaded.getCanonicalName();
                meta.displayName = module == stub ? unloaded.getSimpleName() : module.getName();
                meta.hidden = true;
                meta.java = true;
                meta.author = module.getAuthor();
                meta.description = module.getDescription();
                meta.main = module == stub ? unloaded.getName() : module.getClass().getName();
                meta.minGameVersion = module.getMinGameVersion();
                meta.version = module.getVersion();
                Mods.LoadedMod loadedMod = new Mods.LoadedMod(currentJar, root, module, module.getClass().getClassLoader(), meta);
                module.thisLoaded = loadedMod;
                try {
                    module.preInit();
                }catch(Throwable t){
                    try {module.handleError(t);}catch(Throwable ignored){}
                    ;
                }
                Vars.mods.list().add(loadedMod);
        
            }catch(Throwable e){
                WarningHandler.handleProgrammerFault(e);
            }
        }
        Log.infoTag("Glopion-Module-Register", "Registered: " + count + " modules");
        unloadedModules = loadedModules;
    }
    
    @Override
    public void postInit() {
        if (postInit) throw new RuntimeException("PostInit Already Loaded");
        postInit = true;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Class<? extends Module> c : unloadedModules) {
            sb.append(c.getSimpleName()).append(".class").append(",");
            try {
                Module m = modules.get(c);
                m.postInit();
            }catch(Throwable e){
                try {modules.get(c).handleError(e);}catch(Throwable ignored){}
                Log.errTag("Glopion-Module-Post-Init", "Failed: " + c.getName());
                WarningHandler.handleMindustry(e);
            }
        }
        sb.append("]");
        Log.debug(sb.toString());
    }
    
    
}
