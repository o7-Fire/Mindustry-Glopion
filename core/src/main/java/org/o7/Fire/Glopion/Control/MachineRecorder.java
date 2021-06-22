package org.o7.Fire.Glopion.Control;

import mindustry.gen.Player;
import org.o7.Fire.Glopion.Module.Module;
import org.o7.Fire.Glopion.Module.WorldModule;

public class MachineRecorder implements Module, WorldModule {
    protected Player player;
    public MachineRecorder(Player p){
        player = p;
    }
    
    @Override
    public void update() {
    
    }
}
