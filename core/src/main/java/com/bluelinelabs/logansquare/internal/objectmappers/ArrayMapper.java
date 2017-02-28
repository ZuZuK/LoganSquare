package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.Arrays;
import java.util.List;

/**
 * Built-in mapper for ArrayList objects of an specific type
 */
@SuppressWarnings("unchecked")
public class ArrayMapper<TItem> extends SingleParameterCollectionMapper<TItem, TItem[]> {

    public ArrayMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected TItem[] toCollection(List<TItem> sourceList) {
        return (TItem[]) sourceList.toArray();
    }

    @Override
    protected List<TItem> toList(TItem[] sourceCollection) {
        return Arrays.asList(sourceCollection);
    }

}
