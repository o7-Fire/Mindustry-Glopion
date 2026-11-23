package org.o7.Fire.Glopion.Brain.Classification;

import Atom.Utility.Pool;

import java.util.concurrent.Future;

public interface ImageClassifier {
    default Future<ClassificationResult> classifyAsync(Image image) {
        return Pool.submit(() -> classify(image));
    }
    
    ClassificationResult classify(Image image) throws Exception;
}
