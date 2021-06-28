package org.o7.Fire.Glopion.Brain.State;

public interface StateController<T> {
    boolean isDone();
    
    T reset();
    
    boolean isMultiplayer();
    
    Object info();
    
    void nextStep();
    
    double reward();
    
    void close();
}
