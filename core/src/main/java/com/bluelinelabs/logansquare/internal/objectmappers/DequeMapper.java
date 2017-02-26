package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Built-in mapper for Deque objects of an specific type
 */
public class DequeMapper<TItem> extends SingleParameterCollectionMapper<TItem, Deque<TItem>> {

    public DequeMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected Deque<TItem> toCollection(final List<TItem> sourceList) {
        return new ArrayDeque<>(sourceList);
    }

    @Override
    protected List<TItem> toList(final Deque<TItem> sourceCollection) {
        return new ArrayList<>(sourceCollection);
    }

}
