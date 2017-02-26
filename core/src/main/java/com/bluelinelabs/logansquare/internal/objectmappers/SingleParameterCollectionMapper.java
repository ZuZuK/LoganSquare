package com.bluelinelabs.logansquare.internal.objectmappers;

import com.bluelinelabs.logansquare.JsonMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Built-in mapper for List objects of an specific type
 */
public abstract class SingleParameterCollectionMapper<TItem, TCollection extends Collection<TItem>> extends JsonMapper<TCollection> {

    private final JsonMapper<TItem> itemMapper;

    public SingleParameterCollectionMapper(JsonMapper<TItem> itemMapper) {
        this.itemMapper = itemMapper;
    }

    protected abstract TCollection toCollection(List<TItem> sourceList);

    protected abstract List<TItem> toList(TCollection sourceCollection);

    @Override
    public TCollection parse(JsonParser jsonParser) throws IOException {
        return toCollection(itemMapper.parseList(jsonParser));
    }

    @Override
    public void parseField(TCollection instance, String fieldName, JsonParser jsonParser) throws IOException {
    }

    @Override
    public void serialize(TCollection list, JsonGenerator generator, boolean writeStartAndEnd) throws IOException {
        itemMapper.serialize(toList(list), generator);
    }

}
