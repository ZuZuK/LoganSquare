package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.Map;

/**
 * Built-in mapper for List objects of an specific type
 */
public abstract class DoubleParameterCollectionMapper<TItem, TCollection extends Map<String, TItem>> extends JsonMapper<TCollection> {

    private final JsonMapper<TItem> itemMapper;

    public DoubleParameterCollectionMapper(JsonMapper<TItem> itemMapper) {
        this.itemMapper = itemMapper;
    }

    protected abstract TCollection toCollection(Map<String, TItem> sourceList);

    protected abstract Map<String, TItem> toMap(TCollection sourceCollection);

    @Override
    public TCollection parse(JsonParser jsonParser) throws IOException {
        return toCollection(itemMapper.parseMap(jsonParser));
    }

    @Override
    public void parseField(TCollection instance, String fieldName, JsonParser jsonParser) throws IOException {
    }

    @Override
    public void serialize(TCollection list, JsonGenerator generator, boolean writeStartAndEnd) throws IOException {
        itemMapper.serialize(toMap(list), generator);
    }

}
