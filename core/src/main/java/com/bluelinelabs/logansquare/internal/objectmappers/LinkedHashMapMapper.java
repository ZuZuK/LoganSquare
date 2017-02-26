package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Built-in mapper for LinkedHashMap objects of a specific value types
 */
public class LinkedHashMapMapper<TItem> extends DoubleParameterCollectionMapper<TItem, LinkedHashMap<String, TItem>> {

    public LinkedHashMapMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected LinkedHashMap<String, TItem> toCollection(Map<String, TItem> sourceList) {
        return new LinkedHashMap<>(sourceList);
    }

    @Override
    protected Map<String, TItem> toMap(LinkedHashMap<String, TItem> sourceCollection) {
        return sourceCollection;
    }

}
