package org.o7.Fire.Glopion.Event;

import mindustry.gen.Building;
import mindustry.gen.Player;
import org.jetbrains.annotations.Nullable;

public interface WorldEvent {
    default void onTileConfig(@Nullable Player player, Building building, @Nullable Object value) {
    
    }
    
    default void onWoldUnload() throws Exception {//disconnect world
    
    }
    
    default void onWorldLoad() throws Exception {//connect world
    
    }
    
    
}
