package org.o7.Fire.Glopion.Experimental;

import arc.util.Log;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.o7.Fire.Glopion.Control.MachineRecorder;
import org.o7.Fire.Glopion.Internal.Interface;
import org.o7.Fire.Glopion.Module.ModuleRegisterer;

public class PlayerRecorder implements Experimental {
    @Override
    public void run() {
        if(Groups.player.isEmpty()){
            Interface.showInfo("No Player");
            return;
        }
        int i = 0;
        for(Player p : Groups.player){
            MachineRecorder machineRecorder = new MachineRecorder(p);
            boolean b = ModuleRegisterer.add(machineRecorder) == null;
            if(b){
                Log.info("recording @", p.name);
                i++;
            }else {
                Log.info("recording in progress for @", p.name);
            }
            
        }
        Interface.showInfo("Recording " + i + " players");
    }
}
