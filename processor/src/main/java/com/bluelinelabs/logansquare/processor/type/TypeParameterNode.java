package com.bluelinelabs.logansquare.processor.type;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

public class TypeParameterNode {

    public static final TypeParameterNode ANY_TYPE_NODE = new TypeParameterNode();

    public static TypeParameterNode fromTypeMirror(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.TYPEVAR) {
            return new TypeParameterNode(typeMirror.toString());
        }
        if (typeMirror instanceof WildcardType) {
            WildcardType wildcard = (WildcardType) typeMirror;
            if (wildcard.getExtendsBound() != null) {
                return fromTypeMirror(wildcard.getExtendsBound());
            } else if (wildcard.getSuperBound() != null) {
                return fromTypeMirror(wildcard.getSuperBound());
            } else {
                return ANY_TYPE_NODE;
            }
        }
        if (typeMirror instanceof DeclaredType) {
            ArrayList<TypeParameterNode> typeArguments = new ArrayList<>();
            for (TypeMirror typeArgument : ((DeclaredType) typeMirror).getTypeArguments()) {
                typeArguments.add(TypeParameterNode.fromTypeMirror(typeArgument));
            }
            return new TypeParameterNode((DeclaredType) typeMirror, typeArguments);
        }
        //TODO: someday but not today...
        if (typeMirror instanceof ArrayType) {
            throw new IllegalStateException("Arrays in generics currently not supported");
        }
        throw new IllegalStateException("Unexpected kind of type " + typeMirror.getKind());
    }

    public final String typeVarName;
    public final DeclaredType type;
    public final List<TypeParameterNode> typeArguments;

    private TypeParameterNode(String typeVarName, DeclaredType type, List<TypeParameterNode> typeArguments) {
        this.typeVarName = typeVarName;
        this.type = type;
        this.typeArguments = typeArguments;
    }

    private TypeParameterNode() {
        this(null, null, new ArrayList<>());
    }

    private TypeParameterNode(DeclaredType type, List<TypeParameterNode> typeArguments) {
        this(null, type, typeArguments);
    }

    private TypeParameterNode(String typeVarName) {
        this(typeVarName, null, new ArrayList<>());
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