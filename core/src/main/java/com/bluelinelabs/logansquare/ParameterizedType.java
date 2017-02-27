package com.bluelinelabs.logansquare;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;

public class ParameterizedType<T> {

    private static final ParameterizedType[] EMPTY_PARAMETERS_ARRAY = new ParameterizedType[0];

    private static ParameterizedType[] parametersFromType(Type type) {
        if (type instanceof java.lang.reflect.ParameterizedType) {
            Type[] actualTypeArguments = ((java.lang.reflect.ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments != null) {
                ParameterizedType[] typeParameters = new ParameterizedType[actualTypeArguments.length];
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    typeParameters[i] = new ParameterizedType(actualTypeArguments[i]);
                }
                return typeParameters;
            }
        }
        return EMPTY_PARAMETERS_ARRAY;
    }

    private static Class getRawType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof java.lang.reflect.ParameterizedType) {
            return (Class) (((java.lang.reflect.ParameterizedType) type).getRawType());
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            if (((WildcardType) type).getLowerBounds().length == 1) {
                return getRawType(((WildcardType) type).getLowerBounds()[0]);
            } else if (((WildcardType) type).getUpperBounds().length == 1) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);
            } else {
                return Object.class;
            }
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            throw new RuntimeException("Invalid type passed: " + type);
        }
    }

    public final Class rawType;
    public final List<ParameterizedType> typeParameters;
    private final int hashCode;

    public ParameterizedType(Type type) {
        this(getRawType(type), parametersFromType(type));
    }

    public ParameterizedType(Class rawType, ParameterizedType... typeParameters) {
        this.rawType = rawType;
        this.typeParameters = Arrays.asList(typeParameters);
        hashCode = rawType.hashCode() + this.typeParameters.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ParameterizedType that = (ParameterizedType) object;
        return rawType.equals(that.rawType) && typeParameters.equals(that.typeParameters);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

}