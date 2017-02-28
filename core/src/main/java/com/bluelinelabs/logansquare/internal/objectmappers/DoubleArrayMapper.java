package com.bluelinelabs.logansquare.internal.objectmappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for double arrays objects
 */
public class DoubleArrayMapper extends SingleParameterCollectionMapper<Double, double[]> {

    public DoubleArrayMapper() {
        super(new DoubleMapper());
    }

    @Override
    protected double[] toCollection(List<Double> sourceList) {
        double[] result = new double[sourceList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = sourceList.get(i);
        }
        return result;
    }

    @Override
    protected List<Double> toList(double[] sourceCollection) {
        List<Double> result = new ArrayList<>(sourceCollection.length);
        for (int i = 0; i < sourceCollection.length; i++) {
            result.set(i, sourceCollection[i]);
        }
        return result;
    }

}