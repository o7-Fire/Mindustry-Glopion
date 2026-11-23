package org.o7.Fire.Glopion.Module;

import mindustry.gen.Player;
import mindustry.gen.Unit;

public interface UnitModule {
    
    default void onUnitControlEvent(Player player, Unit unit){}
}
