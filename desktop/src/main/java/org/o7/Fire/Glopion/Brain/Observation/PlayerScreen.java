package org.o7.Fire.Glopion.Brain.Observation;

import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.o7.Fire.Glopion.Control.MachineRecorder;

public class PlayerScreen implements Encodable, ObservationSpace<PlayerScreen> {
    protected MachineRecorder machineRecorder;
    public PlayerScreen(MachineRecorder recorder){
        this.machineRecorder = recorder;
    }
    
    @Override
    public double[] toArray() {
        return new double[0];
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
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public int[] getShape() {
        return new int[0];
    }
    
    @Override
    public INDArray getLow() {
        return null;
    }
    
    @Override
    public INDArray getHigh() {
        return null;
    }
}
