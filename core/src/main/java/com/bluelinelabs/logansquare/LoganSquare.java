package com.bluelinelabs.logansquare;

import com.bluelinelabs.logansquare.internal.objectmappers.ArrayDequeMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.ArrayListMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.BooleanMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.DoubleMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.FloatMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.HashMapMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.HashSetMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.IntegerMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.LinkedHashMapMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.LinkedListMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.LongMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.ObjectMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.StringMapper;
import com.bluelinelabs.logansquare.internal.objectmappers.TreeMapMapper;
import com.bluelinelabs.logansquare.typeconverters.DefaultCalendarConverter;
import com.bluelinelabs.logansquare.typeconverters.DefaultDateConverter;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.bluelinelabs.logansquare.util.SimpleArrayMap;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/** The point of all interaction with this library. */
public class LoganSquare {

    private static final Map<ParameterizedType, JsonMapper> OBJECT_MAPPERS = new ConcurrentHashMap<>();

    private static final Map<Class, TypeConverter> ENUM_CONVERTERS = new ConcurrentHashMap<>();

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        OBJECT_MAPPERS.put(new ParameterizedType(String.class), new StringMapper());
        OBJECT_MAPPERS.put(new ParameterizedType(Integer.class), new IntegerMapper());
        OBJECT_MAPPERS.put(new ParameterizedType(Long.class), new LongMapper());
        OBJECT_MAPPERS.put(new ParameterizedType(Float.class), new FloatMapper());
        OBJECT_MAPPERS.put(new ParameterizedType(Double.class), new DoubleMapper());
        OBJECT_MAPPERS.put(new ParameterizedType(Boolean.class), new BooleanMapper());
        OBJECT_MAPPERS.put(new ParameterizedType(Object.class), objectMapper);
        ArrayListMapper<Object> arrayListMapper = new ArrayListMapper<>(objectMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(List.class), arrayListMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(ArrayList.class), arrayListMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(LinkedList.class), new LinkedListMapper<>(objectMapper));
        HashSetMapper<Object> hashSetMapper = new HashSetMapper<>(objectMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(Set.class), hashSetMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(HashSet.class), hashSetMapper);
        HashMapMapper<Object> hashMapMapper = new HashMapMapper<>(objectMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(Map.class), hashMapMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(HashMap.class), hashMapMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(TreeMap.class), new TreeMapMapper<>(objectMapper));
        OBJECT_MAPPERS.put(new ParameterizedType(LinkedHashMap.class), new LinkedHashMapMapper<>(objectMapper));
        ArrayDequeMapper<Object> arrayDequeMapper = new ArrayDequeMapper<>(objectMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(Queue.class), arrayDequeMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(Deque.class), arrayDequeMapper);
        OBJECT_MAPPERS.put(new ParameterizedType(ArrayDeque.class), arrayDequeMapper);
    }

    private static final SimpleArrayMap<Class, TypeConverter> TYPE_CONVERTERS = new SimpleArrayMap<>();

    private static final TypeConverter NULL_CONVERTER = new TypeConverter() {
        @Override
        public Object parse(final JsonParser jsonParser) throws IOException {
            throw new IllegalStateException();
        }

        @Override
        public void serialize(final Object object, final String fieldName, final boolean writeFieldNameForObject, final JsonGenerator jsonGenerator) throws IOException {
            throw new IllegalStateException();
        }
    };

    static {
        registerTypeConverter(Date.class, new DefaultDateConverter());
        registerTypeConverter(Calendar.class, new DefaultCalendarConverter());
    }

    /** The JsonFactory that should be used throughout the entire app. */
    public static final JsonFactory JSON_FACTORY = new JsonFactory();

    /**
     * Parse an object from an InputStream.
     *
     * @param is The InputStream, most likely from your networking library.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     */
    public static <E> E parse(InputStream is, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parse(is);
    }

    /**
     * Parse an object from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     */
    public static <E> E parse(String jsonString, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parse(jsonString);
    }

    /**
     * Parse a parameterized object from an InputStream.
     *
     * @param is The InputStream, most likely from your networking library.
     * @param jsonObjectType The ParameterizedType describing the object. Ex: LoganSquare.parse(is, new ParameterizedType&lt;MyModel&lt;OtherModel&gt;&gt;() { });
     */
    public static <E> E parse(InputStream is, ParameterizedType<E> jsonObjectType) throws IOException {
        return mapperFor(jsonObjectType).parse(is);
    }

    /**
     * Parse a parameterized object from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectType The ParameterizedType describing the object. Ex: LoganSquare.parse(is, new ParameterizedType&lt;MyModel&lt;OtherModel&gt;&gt;() { });
     */
    public static <E> E parse(String jsonString, ParameterizedType<E> jsonObjectType) throws IOException {
        return mapperFor(jsonObjectType).parse(jsonString);
    }

    /**
     * Parse a list of objects from an InputStream.
     *
     * @param is The inputStream, most likely from your networking library.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     */
    public static <E> List<E> parseList(InputStream is, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseList(is);
    }

    /**
     * Parse a list of objects from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     */
    public static <E> List<E> parseList(String jsonString, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseList(jsonString);
    }

    /**
     * Parse a map of objects from an InputStream.
     *
     * @param is The inputStream, most likely from your networking library.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     */
    public static <E> Map<String, E> parseMap(InputStream is, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseMap(is);
    }

    /**
     * Parse a map of objects from a String. Note: parsing from an InputStream should be preferred over parsing from a String if possible.
     *
     * @param jsonString The JSON string being parsed.
     * @param jsonObjectClass The @JsonObject class to parse the InputStream into
     */
    public static <E> Map<String, E> parseMap(String jsonString, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).parseMap(jsonString);
    }

    /**
     * Serialize an object to a JSON String.
     *
     * @param object The object to serialize.
     */
    @SuppressWarnings("unchecked")
    public static <E> String serialize(E object) throws IOException {
        return mapperFor((Class<E>)object.getClass()).serialize(object);
    }

    /**
     * Serialize an object to an OutputStream.
     *
     * @param object The object to serialize.
     * @param os The OutputStream being written to.
     */
    @SuppressWarnings("unchecked")
    public static <E> void serialize(E object, OutputStream os) throws IOException {
        mapperFor((Class<E>)object.getClass()).serialize(object, os);
    }

    /**
     * Serialize a parameterized object to a JSON String.
     *
     * @param object The object to serialize.
     * @param parameterizedType The ParameterizedType describing the object. Ex: LoganSquare.serialize(object, new ParameterizedType&lt;MyModel&lt;OtherModel&gt;&gt;() { });
     */
    @SuppressWarnings("unchecked")
    public static <E> String serialize(E object, ParameterizedType<E> parameterizedType) throws IOException {
        return mapperFor(parameterizedType).serialize(object);
    }

    /**
     * Serialize a parameterized  object to an OutputStream.
     *
     * @param object The object to serialize.
     * @param parameterizedType The ParameterizedType describing the object. Ex: LoganSquare.serialize(object, new ParameterizedType&lt;MyModel&lt;OtherModel&gt;&gt;() { }, os);
     * @param os The OutputStream being written to.
     */
    @SuppressWarnings("unchecked")
    public static <E> void serialize(E object, ParameterizedType<E> parameterizedType, OutputStream os) throws IOException {
        mapperFor(parameterizedType).serialize(object, os);
    }

    /**
     * Serialize a list of objects to a JSON String.
     *
     * @param list The list of objects to serialize.
     * @param jsonObjectClass The @JsonObject class of the list elements
     */
    public static <E> String serialize(List<E> list, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).serialize(list);
    }

    /**
     * Serialize a list of objects to an OutputStream.
     *
     * @param list The list of objects to serialize.
     * @param os The OutputStream to which the list should be serialized
     * @param jsonObjectClass The @JsonObject class of the list elements
     */
    public static <E> void serialize(List<E> list, OutputStream os, Class<E> jsonObjectClass) throws IOException {
        mapperFor(jsonObjectClass).serialize(list, os);
    }

    /**
     * Serialize a map of objects to a JSON String.
     *
     * @param map The map of objects to serialize.
     * @param jsonObjectClass The @JsonObject class of the list elements
     */
    public static <E> String serialize(Map<String, E> map, Class<E> jsonObjectClass) throws IOException {
        return mapperFor(jsonObjectClass).serialize(map);
    }

    /**
     * Serialize a map of objects to an OutputStream.
     *
     * @param map The map of objects to serialize.
     * @param os The OutputStream to which the list should be serialized
     * @param jsonObjectClass The @JsonObject class of the list elements
     */
    public static <E> void serialize(Map<String, E> map, OutputStream os, Class<E> jsonObjectClass) throws IOException {
        mapperFor(jsonObjectClass).serialize(map, os);
    }

    @SuppressWarnings("unchecked")
    private static <E> JsonMapper<E> tryGetCollectionMapper(ParameterizedType<E> type, SimpleArrayMap<ParameterizedType, JsonMapper> partialMappers) {
        JsonMapper<E> mapper;
        if (type.rawType == List.class || type.rawType == ArrayList.class) {
            mapper = new ArrayListMapper(LoganSquare.mapperFor(type.typeParameters.get(0), partialMappers));
        } else if (type.rawType == Map.class || type.rawType == HashMap.class) {
            mapper = new HashMapMapper(LoganSquare.mapperFor(type.typeParameters.get(1), partialMappers));
        } else if (type.rawType == Set.class || type.rawType == HashSet.class) {
            mapper = new HashSetMapper(LoganSquare.mapperFor(type.typeParameters.get(0), partialMappers));
        } else if (type.rawType == LinkedList.class) {
            mapper = new LinkedListMapper(LoganSquare.mapperFor(type.typeParameters.get(1), partialMappers));
        } else if (type.rawType == TreeMap.class) {
            mapper = new TreeMapMapper(LoganSquare.mapperFor(type.typeParameters.get(1), partialMappers));
        } else if (type.rawType == LinkedHashMap.class) {
            mapper = new LinkedHashMapMapper(LoganSquare.mapperFor(type.typeParameters.get(1), partialMappers));
        } else if (type.rawType == Queue.class || type.rawType == Deque.class || type.rawType == ArrayDeque.class) {
            mapper = new ArrayDequeMapper(LoganSquare.mapperFor(type.typeParameters.get(0), partialMappers));
        } else {
            mapper = null;
        }
        if (mapper != null) {
            OBJECT_MAPPERS.put(type, mapper);
        }
        return mapper;
    }

    @SuppressWarnings("unchecked")
    private static <E> JsonMapper<E> getMapper(ParameterizedType<E> type, SimpleArrayMap<ParameterizedType, JsonMapper> partialMappers)
            throws NoSuchMapperException {
        if (partialMappers == null) {
            partialMappers = new SimpleArrayMap<>();
        }

        if (OBJECT_MAPPERS.containsKey(type)) {
            return OBJECT_MAPPERS.get(type);
        } else if (partialMappers.containsKey(type)) {
            return partialMappers.get(type);
        } else {
            try {
                JsonMapper<E> mapper = tryGetCollectionMapper(type, partialMappers);
                if (mapper != null) {
                    return mapper;
                }
                Class<?> mapperClass = Class.forName(type.rawType.getName() + Constants.MAPPER_CLASS_SUFFIX);
                Constructor constructor = mapperClass.getDeclaredConstructors()[0];
                if (type.typeParameters.isEmpty()) {
                    mapper = (JsonMapper<E>) constructor.newInstance();
                } else {
                    Object[] args = new Object[2 + type.typeParameters.size()];
                    args[0] = type;
                    args[args.length - 1] = partialMappers;
                    for (int i = 0; i < type.typeParameters.size(); i++) {
                        args[i + 1] = type.typeParameters.get(i);
                    }
                    mapper = (JsonMapper<E>) constructor.newInstance(args);
                }
                OBJECT_MAPPERS.put(type, mapper);
                return mapper;
            } catch (Exception throwable) {
                return null;
            }
        }
    }

    /**
     * Returns whether or not LoganSquare can handle a given class.
     *
     * @param cls The class for which support is being checked.
     */
    @SuppressWarnings("unchecked")
    public static boolean supports(Class cls) {
        return getMapper(new ParameterizedType<>(cls), null) != null;
    }

    /**
     * Returns whether or not LoganSquare can handle a given ParameterizedType.
     *
     * @param type The ParameterizedType for which support is being checked.
     */
    @SuppressWarnings("unchecked")
    public static boolean supports(ParameterizedType type) {
        return getMapper(type, null) != null;
    }

    public static <E> JsonMapper<E> mapperFor(ParameterizedType<E> type, SimpleArrayMap<ParameterizedType, JsonMapper> partialMappers) throws NoSuchMapperException {
        JsonMapper<E> mapper = getMapper(type, partialMappers);
        if (mapper == null) {
            throw new NoSuchMapperException(type.rawType);
        } else {
            return mapper;
        }
    }

    /**
     * Returns a JsonMapper for a given class that has been annotated with @JsonObject.
     *
     * @param cls The class for which the JsonMapper should be fetched.
     */
    public static <E> JsonMapper<E> mapperFor(Class<E> cls) throws NoSuchMapperException {
        return mapperFor(new ParameterizedType<E>(cls), null);
    }

    /**
     * Returns a JsonMapper for a given class that has been annotated with @JsonObject.
     *
     * @param type The ParameterizedType for which the JsonMapper should be fetched.
     */
    @SuppressWarnings("unchecked")
    public static <E> JsonMapper<E> mapperFor(ParameterizedType<E> type) throws NoSuchMapperException {
        return mapperFor(type, null);
    }

    private static <E> TypeConverter<E> findGeneratedEnumConverterFor(Class<E> cls) {
        if (cls.getSuperclass() == Enum.class) {
            if (!ENUM_CONVERTERS.containsKey(cls)) {
                // The only way the converter wouldn't already be loaded into OBJECT_MAPPERS is if it was compiled separately, but let's handle it anyway
                try {
                    Class<?> enumConverterClass = Class.forName(cls.getName() + Constants.CONVERTER_CLASS_SUFFIX);
                    if (enumConverterClass != null) {
                        ENUM_CONVERTERS.put(cls, (TypeConverter<E>) enumConverterClass.newInstance());
                    } else {
                        ENUM_CONVERTERS.put(cls, NULL_CONVERTER);
                    }
                } catch (Exception ignored) {
                    ENUM_CONVERTERS.put(cls, NULL_CONVERTER);
                }
            }
            TypeConverter typeConverter = ENUM_CONVERTERS.get(cls);
            return typeConverter != NULL_CONVERTER ? typeConverter : null;
        }
        return null;
    }

    /**
     * Returns a TypeConverter for a given class.
     *
     * @param cls The class for which the TypeConverter should be fetched.
     */
    @SuppressWarnings("unchecked")
    public static <E> TypeConverter<E> typeConverterFor(Class<E> cls) throws NoSuchTypeConverterException {
        TypeConverter<E> typeConverter = TYPE_CONVERTERS.get(cls);
        if (typeConverter == null) {
            typeConverter = findGeneratedEnumConverterFor(cls);
        }
        if (typeConverter == null) {
            throw new NoSuchTypeConverterException(cls);
        }
        return typeConverter;
    }

    /**
     * Register a new TypeConverter for parsing and serialization.
     *
     * @param cls The class for which the TypeConverter should be used.
     * @param converter The TypeConverter
     */
    public static <E> void registerTypeConverter(Class<E> cls, TypeConverter<E> converter) {
        TYPE_CONVERTERS.put(cls, converter);
    }
}
