package org.o7.Fire.Glopion.Control;

import Atom.Utility.Random;
import mindustry.Vars;
import mindustry.world.Tile;
import org.o7.Fire.Glopion.GlopionCore;
import org.o7.Fire.Glopion.Module.ModsModule;
import org.o7.Fire.Glopion.Module.WorldModule;

public class ControlInterpreter extends ModsModule implements WorldModule {
    long nextAction = System.currentTimeMillis() + 1000;
    public static MachineRecorder mainPlayerRecorder;
    @Override
    public void update() {
        if(Vars.state.isPlaying() && System.currentTimeMillis() > nextAction){
            //random(MachineControl.mainPlayer);
            /*
            if(GlopionCore.machineVisualizeRenderSettings && mainPlayerRecorder != null){
                Tile[][] tiles = mainPlayerRecorder.getWorldData(MachineRecorder.maxView);
                int[][] visual = MachineRecorder.worldDataToVisual(tiles);
                Vars.ui.hudfrag.setHudText(MachineRecorder.visualizeColorized(visual).toString());
            }
            mainPlayerRecorder.getEnvironmentInformation();
            nextAction = System.currentTimeMillis() + 32;
            
             */
        }
    }
    
    @Override
    public void onWorldLoad() {
        mainPlayerRecorder = new MachineRecorder(Vars.player);
        Tile[][] tiles = mainPlayerRecorder.getWorldData(MachineRecorder.maxView);
        int[][] visual = MachineRecorder.worldDataToVisual(tiles);
        System.out.println(MachineRecorder.visualize(visual));
    }
    
    @Override
    public void postInit() throws Throwable {
        if(Vars.player != null){
            MachineControl.mainPlayer = new MachineControl(Vars.player);
        }
    }
    
    public static void random(MachineControl control){
        interpret(Random.getRandom(Actions.values()),control,Random.getFloat());
    }
    public static void interpret(Actions c, MachineControl control, float value){
        Vars.ui.hudfrag.setHudText(c.name() + ": " + value);
        switch (c){
            case MoveHorizontal:
                control.velocity((value*2)-1f,0);
            case MoveVertical:
                control.velocity(0,(value*2)-1f);
            case Rotate:
                control.rotate(value*360);
            case Shooting:
                control.shoot(value);
            default:
        }
    }
}
