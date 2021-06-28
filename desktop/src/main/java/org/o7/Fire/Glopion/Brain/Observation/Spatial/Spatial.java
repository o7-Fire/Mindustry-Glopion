package org.o7.Fire.Glopion.Brain.Observation.Spatial;
@FunctionalInterface
public interface Spatial<T> {
    float process(T whatEverThisThingIs);
}
