package org.o7.Fire.Glopion.Control;

import java.io.IOException;

public class StateReader extends StateCalculatorReader {
    public int[] vector;
    public int index = 0;
    public StateReader(int[] vector){
        this.vector = vector;
    }
    @Override
    public void write(int b) throws IOException {
        vector[index++] = b;
    }
    
    @Override
    public void reset() {
        super.reset();
        index = 0;
        vector = new int[0];
    }
    public void reset(int[] vector) {
        reset();
        this.vector = vector;
    }
}
