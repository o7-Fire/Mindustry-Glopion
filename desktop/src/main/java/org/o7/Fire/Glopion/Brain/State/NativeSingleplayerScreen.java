package org.o7.Fire.Glopion.Brain.State;

import Atom.Reflect.UnThread;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import arc.Core;
import arc.Events;
import arc.func.Floatc;
import arc.func.Floatp;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.gen.Groups;
import mindustry.maps.Map;
import mindustry.world.Tile;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.o7.Fire.Glopion.Brain.Observation.PlayerObservationScreen;
import org.o7.Fire.Glopion.Control.MachineRecorder;

public class NativeSingleplayerScreen extends PlayerObservationScreen implements StateController {
    public NativeSingleplayerScreen() {
        super(new MachineRecorder(Vars.player), MachineRecorder.maxView);
        Events.run(EventType.Trigger.newGame,this::unlock);
    }
    
    private void unlock() {
        lock = false;
        synchronized (this) {
            notifyAll();
        }
    }
  
    @Override
    public INDArray getData() {
        float[] compiledVector = machineRecorder.compiledVector();
        machineRecorder.incrementAssignCompiledIndex( ()->(float) Math.min(Vars.state.enemies, 1));
        machineRecorder.incrementAssignCompiledIndex( ()->(float) Core.graphics.getHeight() / Core.input.mouseY());
        machineRecorder.incrementAssignCompiledIndex( ()->(float)Core.graphics.getWidth() / Core.input.mouseX());
        machineRecorder.incrementAssignCompiledIndex( ()->(float) Vars.content.getBy(ContentType.block).size / Vars.control.input.block.id);
        return Nd4j.create(compiledVector);
    }
    
    @Override
    public boolean isDone() {
        return Vars.state.gameOver;
    }
    protected volatile boolean lock = false;
    @Override
    public NativeSingleplayerScreen reset() {
        if(!isMultiplayer()){
            Gamemode gamemode = Random.getBool() ? Gamemode.attack : Gamemode.survival;
            Map map = Vars.maps.defaultMaps().random();
            while (!gamemode.valid(map)){
                gamemode = Random.getBool() ? Gamemode.attack : Gamemode.survival;
            }
            Vars.control.playMap(map,  map.applyRules(gamemode));
            lock();
            waitLock();
        }
        machineRecorder = new MachineRecorder(Vars.player);
        return this;
    }
    
    private void waitLock(){
      while (lock){
          try {
              synchronized (this) {
                  wait();
              }
          }catch(InterruptedException e){
              lock = false;
              break;
          }
      }
    }
    private  void lock() {
        lock = true;
    }
    
    @Override
    public boolean isMultiplayer() {
        return Vars.net.active();
    }
    
    @Override
    public Object info() {
        return Vars.state.wave;
    }
    
    public static float undelta(){
        return (float) 3000 / Core.graphics.getFramesPerSecond();
    }
    
    @Override
    public void nextStep() {
        //todo don't
        Vars.control.resume();
        Pool.submit(()->{
            UnThread.sleep((3000 / Core.graphics.getFramesPerSecond()));
            unlock();
        });
        lock();
        waitLock();
    }
    
    @Override
    public double reward() {
        return (double) Vars.player.unit().healthf() +  (Vars.state.stats.enemyUnitsDestroyed/100) + (Vars.state.stats.buildingsBuilt/100) + (Vars.state.stats.wavesLasted / 10) -  (Vars.state.enemies / 100);
    }
    
    @Override
    public void close() {
        if(!isMultiplayer()){
           Vars.ui.pause();
        }
    }
}
