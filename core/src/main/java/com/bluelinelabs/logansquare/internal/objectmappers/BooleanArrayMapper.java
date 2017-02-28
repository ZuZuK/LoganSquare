package com.bluelinelabs.logansquare.internal.objectmappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for boolean arrays objects
 */
public class BooleanArrayMapper extends SingleParameterCollectionMapper<Boolean, boolean[]> {

    public BooleanArrayMapper() {
        super(new BooleanMapper());
    }

    @Override
    protected boolean[] toCollection(List<Boolean> sourceList) {
        boolean[] result = new boolean[sourceList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sourceList.get(i);
        }
        return result;
    }

    @Override
    protected List<Boolean> toList(boolean[] sourceCollection) {
        List<Boolean> result = new ArrayList<>(sourceCollection.length);
        for (int i = 0; i < sourceCollection.length; i++) {
            result.set(i, sourceCollection[i]);
        }
        return result;
    }

}
