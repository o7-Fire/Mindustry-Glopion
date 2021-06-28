package org.o7.Fire.Glopion.Brain.Observation.Spatial;

import mindustry.gen.*;
import mindustry.world.Tile;

public abstract class SpatialExtractor {
    SpatialInformationExtractor parent;
    float[] vector, low, high;

    protected void set(SpatialInformationExtractor parent, float[] vector){
        this.parent = parent;
        this.vector = vector;
    }
    protected void assign(float f){
        vector[parent.index] = f;
        parent.index++;
    }
    protected float high(int i){
        return high[i];
    }
    protected float low(int i){
        return low[i];
    }
  
    abstract int size();
    protected void process(Building building){
    
    }
    protected void process(Tile tile){
    
    }
    protected void process(Unit bulletc){
    
    }
    protected void process(Bullet bulletc){
    
    }
    

}
