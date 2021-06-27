package org.o7.Fire.Glopion.Brain.Observation;

import mindustry.gen.Player;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.o7.Fire.Glopion.Control.MachineRecorder;

public class PlayerObservation implements Encodable {
    protected final Player player;
    public PlayerObservation(Player player){
        this.player = player;
    }
    @Override
    public double[] toArray() {
        return new double[]{player.getX(), player.getY()};
    }
    
    @Override
    public boolean isSkipped() {
        return false;
    }
    
    @Override
    public INDArray getData() {
        return null;
    }
    
    @Override
    public Encodable dup() {
        return null;
    }
}
