package org.o7.Fire.Glopion.Module;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mods;
import org.o7.Fire.Glopion.Commands.CommandsHandler;
import org.o7.Fire.Glopion.Commands.Pathfinding;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Overlay;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Internal.TilesOverlay;
import org.o7.Fire.Glopion.Module.Patch.UIPatch;
import org.o7.Fire.Glopion.Module.Patch.VarsPatch;
import org.o7.Fire.Glopion.Patch.AtomicLogger;
import org.o7.Fire.Glopion.Patch.EventHooker;
import org.o7.Fire.Glopion.Patch.SchematicPool;
import org.o7.Fire.Glopion.Patch.Translation;
import org.o7.Fire.Glopion.Watcher.BlockWatcher;

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
        unloadedModules.addAll(Arrays.asList(AtomicLogger.class, SchematicPool.class, VarsPatch.class, Overlay.class, EventHooker.class, Pathfinding.class, BlockWatcher.class, CommandsHandler.class, Translation.class, UIPatch.class, TilesOverlay.class, ModuleIcon.class));
        if (!Vars.mobile){
            unloadedModules.addAll(InformationCenter.getExtendedClass(ModsModule.class));
        }
    }
    
    public void preInit() {
        if (preInit) throw new RuntimeException("PreInit Already Loaded");
        preInit = true;
        unloadedModules.remove(Module.class);
        unloadedModules.remove(ModsModule.class);
    
        HashSet<Class<? extends ModsModule>> loadedModules = new HashSet<>();
        Fi currentJar = new Fi(InformationCenter.getCurrentJar());
        Fi root = currentJar.parent();
        ModsModule stub = new ModsModule() {};
        int count = 0;
        while (!unloadedModules.isEmpty()) {
            Class<? extends ModsModule> unloaded = unloadedModules.iterator().next();
            unloadedModules.remove(unloaded);
            if (unloaded.getCanonicalName() == null) continue;//cryptic class/anon class
            if (modules.containsKey(unloaded)) continue;
            count++;
        
            try {
                String qualifiedName = unloaded.getCanonicalName().toLowerCase(Locale.ROOT).replace(" ", "-");
                boolean enabled = Core.settings.getBool("mod-" + qualifiedName + "-enabled", true);
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
                    modules.put(unloaded, null);
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
        unloadedModules = loadedModules;
    }
    
    @Override
    public void init() {
        if (init) throw new RuntimeException("Init Already Loaded");
        init = true;
        int maxIteration = unloadedModules.size() * 10;
        int iteration = 0;
        ArrayList<Class<? extends ModsModule>> remaining = new ArrayList<>(unloadedModules);
        while (!remaining.isEmpty()) {
            iteration++;
            if (iteration > maxIteration) throw new RuntimeException("Maximum Iteration Reached, Something is wrong");
            Class<? extends ModsModule> unloaded = remaining.remove(0);
            ModsModule module = modules.get(unloaded);
            if (module == null || !module.thisLoaded.missingDependencies.isEmpty()){
                modules.remove(unloaded);
                unloadedModules.remove(unloaded);
                continue;
            }
            if (module.isLoaded()) continue;
            boolean good = module.dependencySatisfied();
            if (module.missingDependency || module.circularDependency){
                unloadedModules.remove(unloaded);
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
        Events.on(EventType.ClientLoadEvent.class, s -> postInit());
    }
    
    @Override
    public void postInit() {
        if (postInit) throw new RuntimeException("PostInit Already Loaded");
        postInit = true;
        StringBuilder sb = new StringBuilder();
        sb.append("ModsModule: [");
        for (Class<? extends Module> c : unloadedModules) {
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
    }
    
    
}
