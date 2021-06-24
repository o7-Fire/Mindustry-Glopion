package org.o7.Fire.Glopion.Brain;

import Atom.Utility.Random;

public enum NeuralFunction {
    Identity(NeuralFunction::Identity), Relu(NeuralFunction::Relu),  Binary(NeuralFunction::Binary), Normalize(NeuralFunction::normalize);
    
    transient NeuronFunction function;
    
    NeuralFunction(NeuronFunction f) {
        function = f;
    }
    
  
    
    public static int Binary(int val) {
        return val > 0 ? 1 : 0;
    }
    
    public static int Identity(int val) {
        return val;
    }
    
    public static int Relu(int val) {
        return Math.min(Math.max(0, val), 100);
    }
    

    public static int normalize(int val) {
        if (val > 1) return 1;
        if (val < -1) return 0;
        return val;
    }
    
    public static int Cost(int expected, int output) {
        return expected - output;
    }
    
    public static int loss(int[] output, int[] expected) {
        int d = 0;
        for (int i = 0; i < output.length; i++) {
            d += Math.abs(output[i] - expected[i]);
        }
        return d;
    }
    
    public int process(int f) {
        return function.calculate(f);
    }
    
    public static void assignRandom(int[] d, int min, int max) {
        for (int i = 0; i < d.length; i++) {
            d[i] = Random.getInt(min, max);
        }
    }
    
    public static void assignRandom(int[] d) {
        for (int i = 0; i < d.length; i++) {
            int r = Random.getInt();
            d[i] = d[i] + r;
        }
    }
}
