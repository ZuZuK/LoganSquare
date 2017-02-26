package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in mapper for ArrayList objects of an specific type
 */
@SuppressWarnings("unchecked")
public class ArrayListMapper<TItem> extends SingleParameterCollectionMapper<TItem, ArrayList<TItem>> {

    public ArrayListMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected ArrayList<TItem> toCollection(final List<TItem> sourceList) {
        return (ArrayList<TItem>) sourceList;
    }

    @Override
    protected List<TItem> toList(final ArrayList<TItem> sourceCollection) {
        return sourceCollection;
    }

}
