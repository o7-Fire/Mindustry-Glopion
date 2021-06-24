package org.o7.Fire.Glopion.Brain;

import io.jenetics.Chromosome;
import io.jenetics.IntegerGene;

public class JeneticNeuralNetwork extends RawBasicNeuralNet{
    Chromosome<IntegerGene> chromosome;
  
    public JeneticNeuralNetwork(int[] structure) {
        super(new int[0], structure);
       
    }
    public void set(Chromosome<IntegerGene> genes) {
        chromosome = genes;
    }
    
    @Override
    public void reset() {
        chromosome = null;
    }
    
    @Override
    public int getRaw(int index) {
        if(chromosome != null)
        return chromosome.get(index).intValue();
        return super.getRaw(index);
    }
}
