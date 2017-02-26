package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Built-in mapper for Set objects of an specific type
 */
public class SetMapper<TItem> extends SingleParameterCollectionMapper<TItem, Set<TItem>> {

    public SetMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected Set<TItem> toCollection(final List<TItem> sourceList) {
        return new HashSet<>(sourceList);
    }

    @Override
    protected List<TItem> toList(final Set<TItem> sourceCollection) {
        return new ArrayList<>(sourceCollection);
    }

}
