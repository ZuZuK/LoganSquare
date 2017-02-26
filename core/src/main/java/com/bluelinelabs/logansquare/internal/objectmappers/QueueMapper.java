package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Built-in mapper for Queue objects of an specific type
 */
public class QueueMapper<TItem> extends SingleParameterCollectionMapper<TItem, Queue<TItem>> {

    public QueueMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected Queue<TItem> toCollection(final List<TItem> sourceList) {
        return new ArrayDeque<>(sourceList);
    }

    @Override
    protected List<TItem> toList(final Queue<TItem> sourceCollection) {
        return new ArrayList<>(sourceCollection);
    }

}
