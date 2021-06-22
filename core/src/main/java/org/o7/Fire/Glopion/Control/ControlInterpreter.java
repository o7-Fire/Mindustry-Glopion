package org.o7.Fire.Glopion.Control;

import Atom.Utility.Random;
import mindustry.Vars;
import org.o7.Fire.Glopion.Module.ModsModule;

public class ControlInterpreter extends ModsModule {
    long nextAction = System.currentTimeMillis() + 1000;
    @Override
    public void update() {
        if(Vars.state.isPlaying() && System.currentTimeMillis() > nextAction){
            random(MachineControl.mainPlayer);
            nextAction = System.currentTimeMillis() + 800;
        }
    }
    
    @Override
    public void postInit() throws Throwable {
        if(Vars.player != null){
            MachineControl.mainPlayer = new MachineControl(Vars.player);
        }
    }
    
    public static void random(MachineControl control){
        interpret(Random.getRandom(Control.values()),control,Random.getFloat());
    }
    public static void interpret(Control c, MachineControl control, float value){
        Vars.ui.hudfrag.setHudText(c.name() + ": " + value);
        switch (c){
            case MoveHorizontal:
                control.velocity((value*2)-1f,0);
            case MoveVertical:
                control.velocity(0,(value*2)-1f);
            case Rotate:
                control.rotate(value*360);
            case Shoot:
                control.shoot(value);
            default:
                return;
        }
    }
}
