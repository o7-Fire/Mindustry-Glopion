package org.o7.Fire.Glopion.Brain;

import Atom.Struct.PoolObject;

public class JeneticNetworkPool extends PoolObject<JeneticNeuralNetwork> {
    int[] structure;
    public JeneticNetworkPool(int[] structure){
        this.structure = structure;
      
    }
    @Override
    protected JeneticNeuralNetwork newObject() {
        return new JeneticNeuralNetwork(structure);
    }
}
