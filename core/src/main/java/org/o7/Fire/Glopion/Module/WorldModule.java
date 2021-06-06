package org.o7.Fire.Glopion.Module;

import mindustry.gen.Building;
import mindustry.gen.Player;

public interface WorldModule {
    
    default void onTileConfig(Player player, Building tile, Object value) {}
    
    default void onWorldLoad() {}
    
    default void onWoldUnload() {}
}
