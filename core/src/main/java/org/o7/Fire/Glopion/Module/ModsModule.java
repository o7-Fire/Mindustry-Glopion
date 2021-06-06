package org.o7.Fire.Glopion.Module;

import arc.graphics.Texture;
import mindustry.gen.Icon;
import mindustry.mod.Mod;
import org.o7.Fire.Glopion.Version;

import java.util.ArrayList;

public abstract class ModsModule extends Mod implements Module {
    protected ArrayList<Class<? extends ModsModule>> dependency = new ArrayList<>();
    
    
    public String getDescription() {
        return "what";
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
        return Icon.box.getRegion().texture;
    }
}
