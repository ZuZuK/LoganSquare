package com.bluelinelabs.logansquare.processor.processor;

import com.bluelinelabs.logansquare.Constants;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonIgnore.IgnorePolicy;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;
import com.bluelinelabs.logansquare.processor.JsonFieldHolder;
import com.bluelinelabs.logansquare.processor.JsonObjectHolder;
import com.bluelinelabs.logansquare.processor.JsonObjectHolder.JsonObjectHolderBuilder;
import com.bluelinelabs.logansquare.processor.TextUtils;
import com.bluelinelabs.logansquare.processor.TypeUtils;
import com.bluelinelabs.logansquare.processor.type.TypeParameterNode;
import com.squareup.javapoet.TypeName;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;

public class JsonObjectProcessor extends Processor {

    public JsonObjectProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Class getAnnotation() {
        return JsonObject.class;
    }

    @Override
    public void findAndParseObjects(RoundEnvironment env, Map<String, JsonObjectHolder> jsonObjectMap, Elements elements, Types types) {
        for (Element element : env.getElementsAnnotatedWith(JsonObject.class)) {
            try {
                processJsonObjectAnnotation(element, jsonObjectMap, elements, types);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));

                error(element, "Unable to generate injector for %s. Stack trace incoming:\n%s", JsonObject.class, stackTrace.toString());
            }
        }
    }

    private void processJsonObjectAnnotation(Element element, Map<String, JsonObjectHolder> jsonObjectMap, Elements elements, Types types) {
        TypeElement typeElement = (TypeElement)element;

        if (element.getModifiers().contains(PRIVATE)) {
            error(element, "%s: %s annotation can't be used on private classes.", typeElement.getQualifiedName(), JsonObject.class.getSimpleName());
        }

        JsonObjectHolder holder = jsonObjectMap.get(TypeUtils.getInjectedFQCN(typeElement, elements));
        if (holder == null) {
            String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
            String objectClassName = TypeUtils.getSimpleClassName(typeElement, packageName);
            String injectedSimpleClassName = objectClassName + Constants.MAPPER_CLASS_SUFFIX;
            boolean abstractClass = element.getModifiers().contains(ABSTRACT);
            TypeParameterNode parentTypeParametersInfo = null;
            boolean jsonObjectParentFound = false;

            TypeMirror superclass = typeElement.getSuperclass();
            while (superclass.getKind() != TypeKind.NONE) {
                TypeElement superclassElement = (TypeElement) types.asElement(superclass);
                Map<String, TypeParameterNode> typeParametersValues = new HashMap<>();
                if (parentTypeParametersInfo != null) {
                    for (int i = 0; i < superclassElement.getTypeParameters().size(); i++) {
                        TypeParameterElement typeParameterElement = superclassElement.getTypeParameters().get(i);
                        if (parentTypeParametersInfo.typeArguments.isEmpty()) {
                            typeParametersValues.put(typeParameterElement.getSimpleName().toString(), TypeParameterNode.ANY_TYPE_NODE);
                        } else {
                            typeParametersValues.put(typeParameterElement.getSimpleName().toString(), parentTypeParametersInfo.typeArguments.get(i));
                        }
                    }
                }
                parentTypeParametersInfo = TypeParameterNode.fromTypeMirror(superclass, typeParametersValues);

                if (superclassElement.getAnnotation(JsonObject.class) != null) {
                    jsonObjectParentFound = true;
                    break;
                }

                superclass = superclassElement.getSuperclass();
            }
            if (!jsonObjectParentFound) {
                parentTypeParametersInfo = null;
            }

            JsonObject annotation = element.getAnnotation(JsonObject.class);

            holder = new JsonObjectHolderBuilder()
                    .setPackageName(packageName)
                    .setInjectedClassName(injectedSimpleClassName)
                    .setObjectTypeName(TypeName.get(typeElement.asType()))
                    .setIsAbstractClass(abstractClass)
                    .setParentTypeParametersInfo(parentTypeParametersInfo)
                    .setFieldNamingPolicy(annotation.fieldNamingPolicy())
                    .setSerializeNullObjects(annotation.serializeNullObjects())
                    .setSerializeNullCollectionElements(annotation.serializeNullCollectionElements())
                    .setTypeParameters(typeElement.getTypeParameters())
                    .build();

            FieldDetectionPolicy fieldDetectionPolicy = annotation.fieldDetectionPolicy();
            if (fieldDetectionPolicy == FieldDetectionPolicy.NONPRIVATE_FIELDS || fieldDetectionPolicy == FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS) {
                addAllNonPrivateFields(element, elements, types, holder);
            }
            if (fieldDetectionPolicy == FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS) {
                addAllNonPrivateAccessors(element, elements, types, holder);
            }

            jsonObjectMap.put(TypeUtils.getInjectedFQCN(typeElement, elements), holder);
        }
    }

    private void addAllNonPrivateFields(Element element, Elements elements, Types types, JsonObjectHolder objectHolder) {
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            ElementKind enclosedElementKind = enclosedElement.getKind();
            if (enclosedElementKind == ElementKind.FIELD) {
                Set<Modifier> modifiers = enclosedElement.getModifiers();
                if (!modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.PROTECTED) && !modifiers.contains(Modifier.TRANSIENT) && !modifiers.contains(Modifier.STATIC)) {
                    createOrUpdateFieldHolder(enclosedElement, elements, types, objectHolder);
                }
            }
        }
    }

    private void addAllNonPrivateAccessors(Element element, Elements elements, Types types, JsonObjectHolder objectHolder) {
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            ElementKind enclosedElementKind = enclosedElement.getKind();
            if (enclosedElementKind == ElementKind.FIELD) {
                Set<Modifier> modifiers = enclosedElement.getModifiers();

                if (modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.TRANSIENT) && !modifiers.contains(Modifier.STATIC)) {

                    String getter = JsonFieldHolder.getGetter(enclosedElement, elements);
                    String setter = JsonFieldHolder.getSetter(enclosedElement, elements);

                    if (!TextUtils.isEmpty(getter) && !TextUtils.isEmpty(setter)) {
                        createOrUpdateFieldHolder(enclosedElement, elements, types, objectHolder);
                    }
                }
            }
        }
    }

    private void createOrUpdateFieldHolder(Element element, Elements elements, Types types, JsonObjectHolder objectHolder) {
        JsonIgnore ignoreAnnotation = element.getAnnotation(JsonIgnore.class);
        boolean shouldParse = ignoreAnnotation == null || ignoreAnnotation.ignorePolicy() == IgnorePolicy.SERIALIZE_ONLY;
        boolean shouldSerialize = ignoreAnnotation == null || ignoreAnnotation.ignorePolicy() == IgnorePolicy.PARSE_ONLY;

        if (shouldParse || shouldSerialize) {
            JsonFieldHolder fieldHolder = objectHolder.fieldMap.get(element.getSimpleName().toString());
            if (fieldHolder == null) {
                fieldHolder = new JsonFieldHolder();
                objectHolder.fieldMap.put(element.getSimpleName().toString(), fieldHolder);
            }

            String error = fieldHolder.fill(element, elements, types, null, null, objectHolder, shouldParse, shouldSerialize);
            if (!TextUtils.isEmpty(error)) {
                error(element, error);
            }
        }
    }
}
