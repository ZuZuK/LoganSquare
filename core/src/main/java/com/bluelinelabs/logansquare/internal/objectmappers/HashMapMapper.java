package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Built-in mapper for HashMap objects of a specific value types
 */
@SuppressWarnings("unchecked")
public class HashMapMapper<TItem> extends DoubleParameterCollectionMapper<TItem, HashMap<String, TItem>> {

    public HashMapMapper(JsonMapper<TItem> itemMapper) {
        super(itemMapper);
    }

    @Override
    protected HashMap<String, TItem> toCollection(Map<String, TItem> sourceList) {
        return (HashMap<String, TItem>) sourceList;
    }

    @Override
    protected Map<String, TItem> toMap(HashMap<String, TItem> sourceCollection) {
        return sourceCollection;
    }

}
