package com.bluelinelabs.logansquare.internal.objectmappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for integer arrays objects
 */
public class IntegerArrayMapper extends SingleParameterCollectionMapper<Integer, int[]> {

    public IntegerArrayMapper() {
        super(new IntegerMapper());
    }

    @Override
    protected int[] toCollection(List<Integer> sourceList) {
        int[] result = new int[sourceList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sourceList.get(i);
        }
        return result;
    }

    @Override
    protected List<Integer> toList(int[] sourceCollection) {
        List<Integer> result = new ArrayList<>(sourceCollection.length);
        for (int i = 0; i < sourceCollection.length; i++) {
            result.set(i, sourceCollection[i]);
        }
        return result;
    }

}