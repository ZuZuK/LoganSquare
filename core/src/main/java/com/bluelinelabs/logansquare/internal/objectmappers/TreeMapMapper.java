package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.Map;
import java.util.TreeMap;

/**
 * Built-in mapper for TreeMap objects of a specific value types
 */
public class TreeMapMapper<TItem> extends DoubleParameterCollectionMapper<TItem, TreeMap<String, TItem>> {

    public TreeMapMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected TreeMap<String, TItem> toCollection(Map<String, TItem> sourceList) {
        return new TreeMap<>(sourceList);
    }

    @Override
    protected Map<String, TItem> toMap(TreeMap<String, TItem> sourceCollection) {
        return sourceCollection;
    }

}
