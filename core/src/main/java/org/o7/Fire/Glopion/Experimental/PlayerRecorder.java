package org.o7.Fire.Glopion.Experimental;

import arc.util.Log;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.o7.Fire.Glopion.Control.MachineRecorder;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

public class PlayerRecorder implements Experimental {
    @Override
    public void run() {
        for(Player p : Groups.player){
            MachineRecorder machineRecorder = new MachineRecorder(p);
            boolean b = ModuleRegisterer.add(machineRecorder);
            if(b){
                Log.info("@ is recording", p.name);
            }else {
                Log.info("@ already recorded", p.name);
            }
            
        }
    }
}
