package com.bluelinelabs.logansquare.internal.objectmappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for float arrays objects
 */
public class FloatArrayMapper extends SingleParameterCollectionMapper<Float, float[]> {

    public FloatArrayMapper() {
        super(new FloatMapper());
    }

    @Override
    protected float[] toCollection(List<Float> sourceList) {
        float[] result = new float[sourceList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sourceList.get(i);
        }
        return result;
    }

    @Override
    protected List<Float> toList(float[] sourceCollection) {
        List<Float> result = new ArrayList<>(sourceCollection.length);
        for (int i = 0; i < sourceCollection.length; i++) {
            result.set(i, sourceCollection[i]);
        }
        return result;
    }

}