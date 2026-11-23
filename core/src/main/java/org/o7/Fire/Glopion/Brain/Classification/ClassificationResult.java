package org.o7.Fire.Glopion.Brain.Classification;

public interface ClassificationResult {
    boolean isNsfw();
    
    boolean is(String s);
    
    boolean contain(String s);
    
    double get(String s);
}
