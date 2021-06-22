package org.o7.Fire.Glopion.Control;

import mindustry.gen.Player;
import mindustry.gen.Unit;

public class MachineControl {
    public static MachineControl mainPlayer = null;
   
    protected Player player;
    Unit u;
    public MachineControl(Player p){
        player = p;
        u = player.unit();
    }
    public void velocity(float x, float y){
        player.unit().vel().set(x,y);
    }
   public void move(float x, float y){
        player.unit().move(x,y);
   }
   
   public void rotate(float v){
        player.unit().rotation(v);
   }
    
    public void shoot(float value) {
        player.shooting = value > 0.5f;
    }
}
