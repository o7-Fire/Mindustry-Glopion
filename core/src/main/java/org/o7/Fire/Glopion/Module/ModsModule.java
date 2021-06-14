package org.o7.Fire.Glopion.Module;

import Atom.Reflect.Reflect;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Version;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class ModsModule extends Mod implements Module {
    static Texture def = null;
    public boolean missingDependency, circularDependency;
    protected ArrayList<Class<? extends ModsModule>> dependency = new ArrayList<>();
    protected Mods.LoadedMod thisLoaded = null;
    public HashSet<Class<? extends ModsModule>> missingClass = new HashSet<>(), circularClass = new HashSet<>();
    
    public String getDescription() {
        return "what";
    }
    
    @Override
    public String getName() {
        return "Glopion-" + Module.super.getName();
    }
    
    protected boolean loaded = false;
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public <T extends ModsModule> T getModule(Class<T> c) {
        return c.cast(ModuleRegisterer.modules.get(c));
    }
    
    
    public boolean dependencySatisfied() {
        boolean noGood = false;
        for (Class<? extends ModsModule> mod : dependency) {
            ModsModule mods = getModule(mod);
            if (mods == null || mods.missingDependency){
                missingClass.add(mod);
                missingDependency = true;
                noGood = true;
            }else{
                if (!mods.isLoaded()) noGood = true;
                if (mods.dependency.contains(this.getClass())){
                    circularClass.add(mod);
                    circularDependency = true;
                    if (Reflect.DEBUG_TYPE == Reflect.DebugType.DevEnvironment)
                        throw new RuntimeException("Circular Dependency: " + this.getClass().getCanonicalName() + " <---> " + mod.getCanonicalName());
                }
            }
        }
        return !noGood;
    }
    
    /**
     * only 1 line away from {@link mindustry.game.EventType.ClientLoadEvent} so
     */
    @Override
    public void init() {
        try {
            postInit();
        }catch(Throwable e){
            handleError(e);
            WarningHandler.handleMindustry(e);
            Log.errTag("Glopion-Module-Post-Init", "Failed: " + this.getClass().getCanonicalName());
        }
    }
    
    /**
     * Original Init
     */
    public void start() {
        if (loaded) throw new RuntimeException("Already Loaded: " + thisLoaded.name);
        loaded = true;
    }
    
    protected void handleError(Throwable t) {
        if (thisLoaded != null){
            thisLoaded.state = Mods.ModState.contentErrors;
            Content error = new Content() {
                @Override
                public ContentType getContentType() {
                    return ContentType.error;
                }
            };
            error.minfo.baseError = t;
            error.minfo.mod = thisLoaded;
            error.minfo.sourceFile = new Fi(InformationCenter.getCurrentJar());
            error.minfo.error = Strings.neatError(t);
            thisLoaded.erroredContent.add(error);
            thisLoaded.state = Mods.ModState.contentErrors;
        }
    }
    
    public String getMinGameVersion() {
        return mindustry.core.Version.buildString();
    }
    
    public String getVersion() {
        return Version.buildString();
    }
    
    public String getAuthor() {
        return "o7-Fire, Itzbenz, KovenCrayn, Akimovx, Nexity, Volas171";
    }
    
    public Texture getIcon() {
        if (def != null) return def;
        Mods.LoadedMod l = Vars.mods.getMod("mindustry-glopion");
        if (l == null) return null;
        return def = l.iconTexture;
    }
}
