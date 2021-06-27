package org.o7.Fire.Glopion.Brain.Observation;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;

public class PlayerScreen implements Encodable {
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
}
