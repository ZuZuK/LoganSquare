package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for List objects of an specific type
 */
public class ListMapper<TItem> extends SingleParameterCollectionMapper<TItem, List<TItem>> {

    public ListMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected List<TItem> toCollection(final List<TItem> sourceList) {
        return sourceList;
    }

    @Override
    protected List<TItem> toList(final List<TItem> sourceCollection) {
        return sourceCollection;
    }

}
