package org.o7.Fire.Glopion.Brain;

import com.aparapi.Kernel;

public interface NeuralNetworkAparapi extends RawNeuralNet{
    
    static int[] process(int[] input, final int[] raw, final int[] structure){
        int index = 0;
        for (int i = 0; i < structure.length; i++) {
            int outputSize = structure[i];
            int[] outputLayer = new int[outputSize];
        
            for (int j = 0; j < outputSize; j++) {//for node
                int node = 0;
                for (int v : input) {//node input summation
                    node += v * raw[index];//weight
                    index++;
                }
                node += raw[index];//bias
                index++;
               
            }
            input = outputLayer;
        }
        return input;
    }
    
    default int[] process(int[] input) {
        int index = 0;
        final int[] structure = getOutput();
        
        for (int i = 0; i < structure.length; i++) {
            int outputSize = structure[i];
            int[] outputLayer = new int[outputSize];
        
            for (int j = 0; j < outputSize; j++) {//for node
                int node = 0;
                for (int v : input) {//node input summation
                    node += v * getRaw(index);//weight
                    index++;
                }
                node += getRaw(index);//bias
                index++;
                outputLayer[j] = activation(node);//activation
            }
            input = outputLayer;
        }
        return input;
    }
    
    int[] getOutput();
    
    @Override
    default int error(int[] input, int[] expected) {
        int[] output = process(input);
        int d = 0;
        for (int i = 0; i < output.length; i++) {
            int a = output[i] - expected[i];
            d += (a < 0) ? -a : a;
        }
        return d;
    }
}
