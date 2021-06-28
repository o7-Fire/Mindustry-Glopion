package org.o7.Fire.Glopion.Brain.Observation.Spatial;
@FunctionalInterface
public interface SpatialBoolean<T> extends Spatial<T>{
    boolean processBoolean(T whatEverThisThingIs);
    @Override
    default float process(T whatEverThisThingIs){
        return processBoolean(whatEverThisThingIs) ? 1 : 0;
    }
}
