package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Built-in mapper for LinkedList objects of an specific type
 */
public class LinkedListMapper<TItem> extends SingleParameterCollectionMapper<TItem, LinkedList<TItem>> {

    public LinkedListMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected LinkedList<TItem> toCollection(final List<TItem> sourceList) {
        return new LinkedList<>(sourceList);
    }

    @Override
    protected List<TItem> toList(final LinkedList<TItem> sourceCollection) {
        return sourceCollection;
    }

}