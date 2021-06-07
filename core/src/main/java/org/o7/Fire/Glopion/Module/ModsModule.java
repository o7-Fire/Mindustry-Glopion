package org.o7.Fire.Glopion.Module;

import Atom.Utility.Random;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Strings;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.gen.Icon;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import org.o7.Fire.Glopion.Internal.InformationCenter;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Version;

import java.util.ArrayList;

public abstract class ModsModule extends Mod implements Module {
    protected ArrayList<Class<? extends ModsModule>> dependency = new ArrayList<>();
    protected Mods.LoadedMod thisLoaded = null;
    
    public String getDescription() {
        return "what";
    }
    
    @Override
    public String getName() {
        return "Glopion-" + Module.super.getName();
    }
    
    @Override
    public void init() {
        try {
            start();
        }catch(Throwable t){
            handleError(t);
        }
    }
    
    
    public void start() {
    
    }
    
    protected void handleError(Throwable t) {
        WarningHandler.handleMindustry(t);
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
        }
    }
    
    public String getMinGameVersion() {
        return mindustry.core.Version.buildString();
    }
    
    public String getVersion() {
        return Version.buildString();
    }
    
    public String getAuthor() {
        return "o7-Fire, Itzbenz, KovenCrayn, Akimovx, Nexity";
    }
    
    public Texture getIcon() {
        TextureRegionDrawable ra = Random.getRandom((Iterable<TextureRegionDrawable>) Icon.icons.values());
        if (ra == null) return null;
        return ra.getRegion().texture;
    }
}
