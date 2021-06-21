package org.o7.Fire.Glopion.Desktop.Control;

import Atom.Utility.Random;
import mindustry.Vars;
import org.o7.Fire.Glopion.Module.ModsModule;

public class ControlInterpreter extends ModsModule {
    @Override
    public void update() {
        if(Vars.state.isPlaying()) random(MachineControl.mainPlayer);
    }
    
    public static void random(MachineControl control){
        interpret(Random.getRandom(Control.values()),control,Random.getFloat());
    }
    public static void interpret(Control c, MachineControl control, float value){
        switch (c){
            case MoveHorizontal:
                control.move(0,value);
            case MoveVertical:
                control.move(value,0);
            case Rotate:
                control.rotate(value);
            default:
                return;
        }
    }
}
