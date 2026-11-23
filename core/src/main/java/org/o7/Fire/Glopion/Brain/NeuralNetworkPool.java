package org.o7.Fire.Glopion.Brain;

import Atom.Struct.PoolObject;

public class NeuralNetworkPool extends PoolObject<RawBasicNeuralNet> {
    int inputSize;
    int[] structure;
    public NeuralNetworkPool(int inputSize, int[] structure){
        this.structure = structure;
        this.inputSize = inputSize;
    }
    @Override
    protected RawBasicNeuralNet newObject() {
        return new RawBasicNeuralNet(structure, inputSize);
    }
}
