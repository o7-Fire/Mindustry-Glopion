package org.o7.Fire.Glopion.Brain;

import com.aparapi.Kernel;

public class TrainingKernel extends Kernel {
    
    public static int[][] inputRecord_$constant$, outputRecord_$constant$;
    public static int[] structure_$constant$;
    public static int structureLength, networkInputSize, networkOutputSize;
    public final int[] raw, error = new int[inputRecord_$constant$.length];
    public final int[][] processed;
    public final int errorLength = error.length;
    /*
    public TrainingKernel(int[][] inputRecord_$constant$, int[][] outputRecord_$constant$, int[] structure_$constant$) {
        this.inputRecord_$constant$ = inputRecord_$constant$;
        this.outputRecord_$constant$ = outputRecord_$constant$;
        this.structure_$constant$ = structure_$constant$;
        this.error = new int[inputRecord_$constant$.length];
    }
    
     */
    
    public TrainingKernel(int[] raw) {
        this.raw = raw;
        processed = new int[structureLength][];
        
        for (int i = 0; i < structureLength; i++) {
            processed[i] = new int[structure_$constant$[i]];
        }
       
    }
    public void generateArray(){
    
    }
    public int getError() {
        int err = 0;
        for (int i : error)
            err += i;
        return err;
    }
    
    @Override
    public void run() {
        int j = getGlobalId();
        processed[0] = inputRecord_$constant$[j];
        int index = 0;
        int outputSize = 0, currentInSize = structure_$constant$[0];
        for (int i = 0; i < structureLength; i++) {
            outputSize = structure_$constant$[i+1];
            for (int x = 0; x < outputSize; x++) {//for node
                int node = 0;
                for (int k = 0; k < currentInSize; k++) {
                    int v = processed[i][k];//node input summation
                    node += v * raw[index];//weight
                    index++;
                }
                node += raw[index];//bias
                index++;
                
            }
            currentInSize = outputSize;
        }
        int[] output = processed[structureLength-1];
        int d = 0;
        for (int i = 0; i < networkOutputSize; i++) {
            int a = output[i] - outputRecord_$constant$[j][i];
            d += (a < 0) ? -a : a;
        }
        error[j] = d;
    }
}
