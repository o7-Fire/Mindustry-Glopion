package org.o7.Fire.Glopion.Desktop.Control;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Player;

public class MachineControl {
    public static MachineControl mainPlayer = null;
    static {
        Events.on(EventType.ClientLoadEvent.class,s->{
            if(Vars.player != null)
            mainPlayer = new MachineControl(Vars.player);
        });
    }
    protected Player player;
    public MachineControl(Player p){
        player = p;
    }
    
   public void move(float x, float y){
        player.unit().move(x,y);
   }
   
   public void rotate(float v){
        player.unit().rotation(v);
   }
}
