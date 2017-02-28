package com.bluelinelabs.logansquare.processor.type;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;

public class TypeParameterNode {

    public static final TypeParameterNode ANY_TYPE_NODE = new TypeParameterNode();

    public static TypeParameterNode fromTypeMirror(TypeMirror typeMirror, Map<String, TypeParameterNode> filledParametersValues) {
        if (typeMirror instanceof TypeVariable) {
            if (filledParametersValues.containsKey(typeMirror.toString())) {
                return filledParametersValues.get(typeMirror.toString());
            }
            return new TypeParameterNode(typeMirror.toString());
        }
        if (typeMirror instanceof WildcardType) {
            WildcardType wildcard = (WildcardType) typeMirror;
            if (wildcard.getExtendsBound() != null) {
                return fromTypeMirror(wildcard.getExtendsBound(), filledParametersValues);
            } else if (wildcard.getSuperBound() != null) {
                return fromTypeMirror(wildcard.getSuperBound(), filledParametersValues);
            } else {
                return ANY_TYPE_NODE;
            }
        }
        if (typeMirror instanceof DeclaredType) {
            ArrayList<TypeParameterNode> typeArguments = new ArrayList<>();
            for (TypeMirror typeArgument : ((DeclaredType) typeMirror).getTypeArguments()) {
                typeArguments.add(TypeParameterNode.fromTypeMirror(typeArgument, filledParametersValues));
            }
            return new TypeParameterNode((DeclaredType) typeMirror, typeArguments);
        }
        if (typeMirror instanceof ArrayType) {
            TypeName typeName = TypeName.get(((ArrayType) typeMirror).getComponentType());
            if(!typeName.isPrimitive()) {
                return new TypeParameterNode(TypeParameterNode.fromTypeMirror(((ArrayType) typeMirror).getComponentType(), filledParametersValues));
            } else {
                return new TypeParameterNode(true, typeName.toString());
            }
        }
        throw new IllegalStateException("Unexpected kind of type " + typeMirror.getKind());
    }

    public final boolean isArray;
    public final String typeVarName;
    public final DeclaredType type;
    public final List<TypeParameterNode> typeArguments;

    private TypeParameterNode(String typeVarName, DeclaredType type, List<TypeParameterNode> typeArguments, boolean isArray) {
        this.typeVarName = typeVarName;
        this.type = type;
        this.typeArguments = typeArguments;
        this.isArray = isArray;
    }

    private TypeParameterNode() {
        this(null, null, new ArrayList<>(), false);
    }

    private TypeParameterNode(DeclaredType type, List<TypeParameterNode> typeArguments) {
        this(null, type, typeArguments, false);
    }

    private TypeParameterNode(String typeVarName) {
        this(typeVarName, null, new ArrayList<>(), false);
    }

    private TypeParameterNode(boolean isArray, String typeVarName) {
        this(typeVarName, null, new ArrayList<>(), true);
    }

    private TypeParameterNode(TypeParameterNode typeArgument) {
        this(null, null, Collections.singletonList(typeArgument), true);
    }

    public boolean isGeneric() {
        return typeVarName != null;
    }

    public boolean hasGenericParameters() {
        if (isGeneric()) {
            return true;
        }
        for (TypeParameterNode typeParameterNode : typeArguments) {
            if (typeParameterNode.hasGenericParameters()) {
                return true;
            }
        }
        return false;
    }

}