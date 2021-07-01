package org.o7.Fire.Glopion.Brain.State;

import Atom.Reflect.UnThread;
import Atom.Utility.Pool;
import Atom.Utility.Random;
import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.maps.Map;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.o7.Fire.Glopion.Brain.Observation.PlayerObservationScreen;
import org.o7.Fire.Glopion.Control.MachineRecorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static mindustry.Vars.player;
import static mindustry.Vars.state;

public class NativeSingleplayerScreen extends PlayerObservationScreen implements StateController {
    final List<Float> reward = Collections.synchronizedList(new ArrayList<>());
    public NativeSingleplayerScreen() {
        super(new MachineRecorder(Vars.player), MachineRecorder.maxView);
        Events.run(EventType.Trigger.newGame,this::unlock);
        Events.run(EventType.Trigger.teamCoreDamage, () -> reward.add(-1f));
        Events.on(EventType.UnitDestroyEvent.class, e->{
            if(e.unit.team == Vars.player.team()){
                reward.add(-e.unit.health);
            }else{
                reward.add(e.unit.health);
            }
        });
        Events.on(EventType.WinEvent.class, e->{
           reward.add(1000f);
        });
        Events.on(EventType.LoseEvent.class, e->{
            reward.add(-1000f);
        });
      
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
        if(compiledVector.length != getShape()[0])throw new IndexOutOfBoundsException("Vector: " + compiledVector.length +", shape: " + Arrays.toString(getShape()));
        machineRecorder.incrementAssignCompiledIndex( ()->(float) Math.min(Vars.state.enemies, 1));
        machineRecorder.incrementAssignCompiledIndex( ()->(float) Core.graphics.getHeight() / Core.input.mouseY());
        machineRecorder.incrementAssignCompiledIndex( ()->(float)Core.graphics.getWidth() / Core.input.mouseX());
        machineRecorder.incrementAssignCompiledIndex( ()->(float) Vars.content.getBy(ContentType.block).size / Vars.control.input.block.id);
        machineRecorder.incrementAssignCompiledIndex( ()->(float)  (Vars.control.input.block.isPlaceable() ? 1 : 0));
        machineRecorder.incrementAssignCompiledIndex( ()-> (state.rules.infiniteResources || (player.closestCore() != null && (player.closestCore().items.has(Vars.control.input.block.requirements, state.rules.buildCostMultiplier) || state.rules.infiniteResources))) && player.isBuilder() ? 1 : 0);
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
        double d = 1 - Vars.player.unit().healthf();
        while (!reward.isEmpty())
            d += reward.remove(0);
        return (double) d ;
    }
    
    @Override
    public void close() {
        if(!isMultiplayer()){
           Vars.ui.pause();
        }
    }
}
