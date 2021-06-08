package org.o7.Fire.Glopion.Commands;

import arc.scene.style.Drawable;
import arc.util.pooling.Pool;
import mindustry.gen.Icon;
import org.jetbrains.annotations.Nullable;
import org.o7.Fire.Glopion.Module.Module;

import java.io.PrintStream;

public abstract class CommandsClass implements Module, Pool.Poolable {
    public Drawable icon = Icon.box;
    @Nullable
    public PrintStream output = null;
    
    @Override
    public void reset() {
        output = null;
        icon = null;
    }
}
