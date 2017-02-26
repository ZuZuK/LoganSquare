package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Built-in mapper for HashSet objects of an specific type
 */
public class HashSetMapper<TItem> extends SingleParameterCollectionMapper<TItem, HashSet<TItem>> {

    public HashSetMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected HashSet<TItem> toCollection(final List<TItem> sourceList) {
        return new HashSet<>(sourceList);
    }

    @Override
    protected List<TItem> toList(final HashSet<TItem> sourceCollection) {
        return new ArrayList<>(sourceCollection);
    }

}
