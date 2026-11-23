package org.o7.Fire.Glopion.Brain.Classification;

import arc.util.Disposable;

public interface Image extends Disposable {
    byte[] getData();
    
    byte[] hash();
}
