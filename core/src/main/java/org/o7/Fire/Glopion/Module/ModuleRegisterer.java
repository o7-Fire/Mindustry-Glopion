package org.o7.Fire.Glopion.Module;

import Atom.Struct.UnstableConsumer;
import Atom.Time.Time;
import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Mods;
import org.o7.Fire.Glopion.Commands.CommandsHandler;
import org.o7.Fire.Glopion.Commands.Pathfinding;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Internal.*;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Patch.*;
import org.o7.Fire.Glopion.Watcher.BlockWatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Handle all module creation and initialization, only invoked once
 */
public class ModuleRegisterer implements Module {
    private static HashMap<Class<? extends Module>, Module> modulesSet = new HashMap<>();//contains modsmodule. used to invoke event
    public static HashMap<Class<? extends ModsModule>, ModsModule> modulesMods = new HashMap<>();
    private static HashSet<Class<? extends ModsModule>> unloadedModulesMods = new HashSet<>();
    private volatile static boolean init, preInit, postInit;
    
    public static Module remove(Class<? extends Module> module){
        return modulesSet.remove(module);
    }
    
    public static Module add(Module module){
        Class<? extends Module> moduleClass = module.getClass();
        return modulesSet.put(moduleClass,module);
    }
    
    public static <T> void invokeAll(Class<T> tClass, UnstableConsumer<T> consumer) {
        invokeAll(module -> {
            if (tClass.isInstance(module)){
                consumer.accept((T) module);
            }
        });
    }
    
    public static void invokeAll(UnstableConsumer<Module> m) {
        for (Module value : modulesSet.values()) {
            try {
                m.accept(value);
            }catch(Throwable t){
                Log.err(value.getClass().getCanonicalName());
                WarningHandler.handleProgrammerFault(t);
            }
        }
    }
    
    public static ModsModule registerModule(Class<? extends ModsModule> c) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (modulesMods.containsKey(c))
            return modulesMods.get(c);
        ModsModule m = c.getDeclaredConstructor().newInstance();
        modulesSet.put(c, m);
        return m;
    }
    
    public static Module remove(Module module) {
        return remove(module.getClass());
    }
    
    public static Module get(Class<? extends Module> clz) {
        return modulesSet.get(clz);
    }
    
    
    public void core() {
        unloadedModulesMods.addAll(Arrays.asList(AtomicLogging.class, SchematicPool.class, VarsPatch.class, Overlay.class, EventHooker.class, Pathfinding.class, BlockWatcher.class, CommandsHandler.class, TextManager.class, UIPatch.class, TilesOverlay.class, ModuleIcon.class));
        unloadedModulesMods.add(ModuleInformation.class);//added in 0.6.6
        unloadedModulesMods.add(TranslateChat.class);//added in 0.7.0
        if (!Vars.mobile){
            unloadedModulesMods.addAll(InformationCenter.getExtendedClass(ModsModule.class));
        }
    }
    
    public void preInit() {
        if (preInit) throw new RuntimeException("PreInit Already Loaded");
        preInit = true;
        unloadedModulesMods.remove(Module.class);
        unloadedModulesMods.remove(ModsModule.class);
    
        HashSet<Class<? extends ModsModule>> loadedModules = new HashSet<>();
        Fi currentJar = new Fi(InformationCenter.getCurrentJar());
        Fi root = currentJar.parent();
        ModsModule stub = new ModsModule() {};
        int count = 0;
        while (!unloadedModulesMods.isEmpty()) {
            Class<? extends ModsModule> unloaded = unloadedModulesMods.iterator().next();
            unloadedModulesMods.remove(unloaded);
            if (unloaded.getCanonicalName() == null) continue;//cryptic class/anon class
            if (modulesMods.containsKey(unloaded)) continue;
            count++;
        
            try {
                String qualifiedName = unloaded.getCanonicalName().toLowerCase(Locale.ROOT).replace(" ", "-");
                boolean enabled = Core.settings.getBool("mod-" + qualifiedName + "-enabled", true);
                ModsModule module = stub;
                Mods.ModMeta meta = new Mods.ModMeta();
            
                if (enabled){
                    module = registerModule(unloaded);
                    if (module.disabled()){
                        modulesMods.remove(unloaded);
                        modulesSet.remove(unloaded);
                        continue;
        
                    }
                    modulesMods.put(unloaded, module);
                    ArrayList<Class<? extends ModsModule>> depend = module.dependency;
                    for (Class<? extends ModsModule> h : depend) {
                        meta.dependencies.add(h.getCanonicalName().toLowerCase(Locale.ROOT).replace(" ", "-"));
                        if (modulesMods.containsKey(h)) continue;
                        loadedModules.add(h);
                        unloadedModulesMods.add(h);
                    }
                
                }else{
                    modulesMods.put(unloaded, null);
                }
                meta.name = qualifiedName;
                meta.displayName = module == stub ? "Glopion-" + unloaded.getSimpleName() + "-Disabled" : module.getName();
                meta.hidden = true;
                meta.java = true;
                meta.author = module.getAuthor();
                meta.description = module.getDescription();
                meta.main = module == stub ? unloaded.getName() : module.getClass().getName();
                meta.minGameVersion = module.getMinGameVersion();
                meta.version = module.getVersion();
                Mods.LoadedMod loadedMod = new Mods.LoadedMod(currentJar, root, module, module.getClass().getClassLoader(), meta);
                module.thisLoaded = loadedMod;
                if (enabled){
                    try {
                        module.preInit();
                    }catch(Throwable t){
                        try {module.handleError(t);}catch(Throwable ignored){}
                        WarningHandler.handleMindustry(t);
                        Log.errTag("Glopion-Module-Pre-Init", "Failed: " + unloaded.getCanonicalName());
                    }
                }else{
                    loadedMod.state = Mods.ModState.disabled;
                }
                Vars.mods.list().add(loadedMod);
                loadedModules.add(unloaded);
            }catch(Throwable e){
                WarningHandler.handleProgrammerFault(e);
                Log.errTag("Glopion-Module-Register", "Failed: " + unloaded.getCanonicalName());
            }
        }
        Log.infoTag("Glopion-Module-Register", "Registered: " + count + " modules");
        unloadedModulesMods = loadedModules;
    }
    
    @Override
    public void init() {
        if (init) throw new RuntimeException("Init Already Loaded");
        init = true;
        int maxIteration = unloadedModulesMods.size() * 10;
        int iteration = 0;
        ArrayList<Class<? extends ModsModule>> remaining = new ArrayList<>(unloadedModulesMods);
        while (!remaining.isEmpty()) {
            iteration++;
            if (iteration > maxIteration) throw new RuntimeException("Maximum Iteration Reached, Something is wrong");
            Class<? extends ModsModule> unloaded = remaining.remove(0);
            ModsModule module = modulesMods.get(unloaded);
            if (module == null || !module.thisLoaded.missingDependencies.isEmpty()){
                modulesMods.remove(unloaded);
                unloadedModulesMods.remove(unloaded);
                continue;
            }
            if (module.isLoaded()) continue;
            boolean good = module.dependencySatisfied();
            if (module.missingDependency || module.circularDependency){
                unloadedModulesMods.remove(unloaded);
                StringBuilder reason = new StringBuilder();
                if (module.missingDependency){
                    reason.append("Missing Dependency ");
                    for (Class c : module.missingClass)
                        reason.append(c.getSimpleName()).append(" ");
                    RuntimeException r = new RuntimeException(reason.toString());
                    module.thisLoaded.state = Mods.ModState.missingDependencies;
                    WarningHandler.handleMindustry(r);
                    Log.warn("Removing: @ because @", unloaded.getSimpleName(), reason.toString());
                    reason = new StringBuilder();
                }
                if (module.circularDependency){
                    reason.append("Circular Dependency: ");
                    for (Class c : module.circularClass)
                        reason.append(c.getSimpleName()).append(" ");
                    RuntimeException r = new RuntimeException(reason.toString());
                    module.handleError(r);
                    module.thisLoaded.state = Mods.ModState.contentErrors;
                    WarningHandler.handleMindustry(r);
                    Log.warn("Removing: @ because @", unloaded.getSimpleName(), reason.toString());
                }
                
                continue;
            }
            
            if (good){
                try {
                    module.start();
                    if (Testing.isTestMode()) module.test();
                    continue;
                }catch(Throwable t){
                    try {module.handleError(t);}catch(Throwable ignored){}
                    WarningHandler.handleMindustry(t);
                    Log.errTag("Glopion-Module-Init", "Failed: " + unloaded.getCanonicalName());
                }
            }
            remaining.add(remaining.size(), unloaded);
        }
        Log.debug("Glopion-Module-Registerer: took " + iteration + " to start");
    
    }
    
    @Override
    public void postInit() {
        if (postInit) throw new RuntimeException("PostInit Already Loaded");
        postInit = true;
        StringBuilder sb = new StringBuilder();
        sb.append("ModsModule: [");
        for (Class<? extends Module> c : unloadedModulesMods) {
            sb.append(c.getSimpleName()).append(".class").append(", ");
            /*
            try {
                Module m = modules.get(c);
                m.postInit();
            }catch(Throwable e){
                try {modules.get(c).handleError(e);}catch(Throwable ignored){}
                WarningHandler.handleMindustry(e);
                Log.errTag("Glopion-Module-Post-Init", "Failed: " + c.getCanonicalName());
            }
            
             */
        }
        sb.append("]");
        Log.debug(sb.toString());
        GlopionCore.loadFinishedTime = new Time(TimeUnit.MILLISECONDS);
    
    }
    
    
}
