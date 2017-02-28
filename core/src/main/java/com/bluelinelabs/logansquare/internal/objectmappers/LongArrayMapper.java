package com.bluelinelabs.logansquare.internal.objectmappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for long arrays objects
 */
public class LongArrayMapper extends SingleParameterCollectionMapper<Long, long[]> {

    public LongArrayMapper() {
        super(new LongMapper());
    }

    @Override
    protected long[] toCollection(List<Long> sourceList) {
        long[] result = new long[sourceList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sourceList.get(i);
        }
        return result;
    }

    @Override
    protected List<Long> toList(long[] sourceCollection) {
        List<Long> result = new ArrayList<>(sourceCollection.length);
        for (int i = 0; i < sourceCollection.length; i++) {
            result.set(i, sourceCollection[i]);
        }
        return result;
    }

}