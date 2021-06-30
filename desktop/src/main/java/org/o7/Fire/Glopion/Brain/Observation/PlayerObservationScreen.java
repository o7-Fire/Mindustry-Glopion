package org.o7.Fire.Glopion.Brain.Observation;

import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.o7.Fire.Glopion.Control.MachineRecorder;

public class PlayerObservationScreen implements Encodable, ObservationSpace<PlayerObservationScreen> {
    protected MachineRecorder machineRecorder;
    protected final int radius, diamater;
    public PlayerObservationScreen(MachineRecorder recorder, int radius){
        this.machineRecorder = recorder;
        this.radius = radius;
        this.diamater = radius + radius;
    }
    
    @Override
    public double[] toArray() {
        return new double[0];
    }
    
    @Override
    public boolean isSkipped() {
        return false;
    }
    
    //TODO add what block AI selected, Mouse Coordinate Normalized
    @Override
    public INDArray getData() {
        return Nd4j.create( MachineRecorder.worldDataToVisualFlat(machineRecorder.getWorldData(radius)));
    }
    
    @Override
    public Encodable dup() {
        return new PlayerObservationScreen(machineRecorder,radius);
    }
    
    @Override
    public String getName() {
        return "Plater Screen";
    }
    
    @Override
    public int[] getShape() {
        return new int[]{ diamater*diamater};
    }
    
    @Override
    public INDArray getLow() {
        return PlayerObservationTensor.def;
    }
    
    @Override
    public INDArray getHigh() {
        return PlayerObservationTensor.def;
    }
}
