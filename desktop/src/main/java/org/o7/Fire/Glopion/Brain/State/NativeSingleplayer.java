package org.o7.Fire.Glopion.Brain.State;

import Atom.Reflect.UnThread;
import Atom.Utility.Pool;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Player;
import org.o7.Fire.Glopion.Brain.Observation.PlayerObservation;
import org.o7.Fire.Glopion.Control.MachineRecorder;

public class NativeSingleplayer extends PlayerObservation implements StateController {
    public NativeSingleplayer() {
        super(Vars.player, MachineRecorder.maxView);
        Events.run(EventType.Trigger.newGame,this::unlock);
    }
    
    private void unlock() {
        lock = false;
        synchronized (this) {
            notifyAll();
        }
    }
    
    @Override
    public boolean isDone() {
        return Vars.state.gameOver;
    }
    protected volatile boolean lock = false;
    @Override
    public NativeSingleplayer reset() {
        if(!isMultiplayer()){
            Vars.control.playMap(Vars.state.map, Vars.state.rules);
            lock();
            waitLock();
        }
        player = Vars.player;
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
    
    @Override
    public void nextStep() {
        //todo don't
        Pool.submit(()->{
            UnThread.sleep(250);
            unlock();
        });
        lock();
        waitLock();
    }
    
    @Override
    public double reward() {
        return (double) Vars.state.stats.wavesLasted / 100;
    }
    
    @Override
    public void close() {
        if(!isMultiplayer()){
           Vars.ui.pause();
        }
    }
}
