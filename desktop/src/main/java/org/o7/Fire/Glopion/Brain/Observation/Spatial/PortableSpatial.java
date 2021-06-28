package org.o7.Fire.Glopion.Brain.Observation.Spatial;

public class PortableSpatial<T> implements Spatial<T> {
    protected Spatial<T> spatial;
    protected float high, low;
    public Class<T> tClass;
    protected PortableSpatial(){
    
    }
    public PortableSpatial(float high, float min, Class<T> tClass,  Spatial<T> spatial){
        this.spatial = spatial;
        this.high = high;
        this.low = min;
        this.tClass = tClass;
    }
  
    public PortableSpatial(Class<T> tClass,  SpatialBoolean<T> spatial){
        this.spatial = spatial;
        this.high = 1;
        this.low = 0;
        this.tClass = tClass;
    }
    public float getHigh() {
        return high;
    }
    
    public float getLow() {
        return low;
    }
  
    @Override
    public float process(T whatEverThisThingIs) {
        if(whatEverThisThingIs == null)return 0;
        float f = spatial.process(whatEverThisThingIs);
        if(f > high)
            high = f;
        if(f < low)
            low = f;
        return f;
    }
}
