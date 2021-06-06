package org.o7.Fire.Glopion.Commands;

import arc.scene.style.Drawable;
import mindustry.gen.Icon;
import org.o7.Fire.Glopion.Module.Module;

public interface CommandsClass extends Module {
    default Drawable getIcon() {
        return Icon.box;
    }
}
