package org.o7.Fire.Glopion.Control;

public interface InterfaceControl {
    
    float low(int index);
    
    float high(int index);
    
    int sizeInput();
    
    void rawInput(float data, int index);
}
