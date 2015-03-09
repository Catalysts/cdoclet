package cc.catalysts.cdoclet.generator.utils;

import cc.catalysts.cdoclet.generator.Generator;
import cc.catalysts.cdoclet.map.TypeMap;
import com.sun.javadoc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author Catalysts Gmbh
 */
public final class GeneratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(GeneratorUtils.class);

    private static Map<String, cc.catalysts.cdoclet.generator.Type> enumerationTypes = new HashMap<String, cc.catalysts.cdoclet.generator.Type>();

    public static cc.catalysts.cdoclet.generator.Type getType(MethodDoc doc, Generator generator, Collection<String> ignore) {
        logger.debug("Generating type {} <{}>", doc.name(), doc.getClass().getCanonicalName());

        Map<String, cc.catalysts.cdoclet.generator.Type> bounds = new HashMap<String, cc.catalysts.cdoclet.generator.Type>();
        Collection<cc.catalysts.cdoclet.generator.Type> arguments = new ArrayList<cc.catalysts.cdoclet.generator.Type>();

        logger.debug("Parameters {}", Arrays.toString(doc.typeParameters()));
        for (Type argument : doc.typeParameters()) {
            processArgument(argument, generator, arguments, bounds, ignore, new HashSet<String>());
        }

        String name = generator.getTypeMap().getType(doc.name(), !arguments.isEmpty(), false);
        name = generator.getPackageMap().getType(name, !arguments.isEmpty(), false);

        if ("null".equals(name)) return cc.catalysts.cdoclet.generator.Type.NULL;
        return generator.postProcessType(new cc.catalysts.cdoclet.generator.Type(name, arguments, bounds, 0, true, generator.getClassMap()));
    }

    private static void processArgument(Type argument, Generator generator, Collection<cc.catalysts.cdoclet.generator.Type> arguments, Map<String, cc.catalysts.cdoclet.generator.Type> bounds, Collection<String> ignore, Collection<String> visited) {
        logger.debug("Processing argument {} <{}>", argument.qualifiedTypeName(), argument.getClass().getCanonicalName());

        cc.catalysts.cdoclet.generator.Type argumentType = getType(argument, generator, ignore, visited);
        if (argumentType != cc.catalysts.cdoclet.generator.Type.NULL) {
            getTypeBounds(argument, generator, bounds, ignore, visited);
            if (arguments != null) arguments.add(argumentType);
        }
    }

    private static cc.catalysts.cdoclet.generator.Type getType(Type type, Generator generator, Collection<String> ignore, Collection<String> visited) {
        logger.debug("Generating type {} <{}>", type.qualifiedTypeName(), type.getClass().getCanonicalName());

        if (ignore.contains(type.qualifiedTypeName())) return cc.catalysts.cdoclet.generator.Type.NULL;

        if (visited.contains(type.qualifiedTypeName())) return getType(type.qualifiedTypeName(), generator);
        visited.add(type.qualifiedTypeName());

        cc.catalysts.cdoclet.generator.Type enumerationType = getEnumerationType(type, generator);
        if (enumerationType != cc.catalysts.cdoclet.generator.Type.EMPTY) return enumerationType;

        Map<String, cc.catalysts.cdoclet.generator.Type> bounds = new HashMap<String, cc.catalysts.cdoclet.generator.Type>();
        Collection<cc.catalysts.cdoclet.generator.Type> arguments = new ArrayList<cc.catalysts.cdoclet.generator.Type>();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = type.asParameterizedType();
            logger.debug("Arguments {}", Arrays.toString(parameterizedType.typeArguments()));
            for (Type argument : parameterizedType.typeArguments()) {
                processArgument(argument, generator, arguments, bounds, ignore, visited);
            }
        } else if (type instanceof ClassDoc) {
            ClassDoc classDoc = type.asClassDoc();
            logger.debug("Parameters {}", Arrays.toString(classDoc.typeParameters()));
            for (Type argument : classDoc.typeParameters()) {
                processArgument(argument, generator, arguments, bounds, ignore, visited);
            }
        }

        String name = generator.getTypeMap().getType(type.qualifiedTypeName(), !arguments.isEmpty(), isEnum(type));
        name = generator.getPackageMap().getType(name, !arguments.isEmpty(), isEnum(type));

        if ("null".equals(name)) return cc.catalysts.cdoclet.generator.Type.NULL;
        if (name.endsWith("<>")) {
            arguments.clear();
            bounds.clear();
            name = name.substring(0, name.length() - 2);
        }
        return generator.postProcessType(new cc.catalysts.cdoclet.generator.Type(name, arguments, bounds, getDimensions(type), type instanceof TypeVariable, generator.getClassMap()));
    }

    private static cc.catalysts.cdoclet.generator.Type getEnumerationType(Type enumerationType, Generator generator) {
        Class<? extends Annotation> enumAnnotation = generator.getEnumAnnotation();
        ClassDoc classDoc = enumerationType.asClassDoc();
        String typeName = enumerationType.qualifiedTypeName();

        if (generator.hasEnumSupport()) return cc.catalysts.cdoclet.generator.Type.EMPTY;
        if (enumerationTypes.containsKey(typeName)) return enumerationTypes.get(typeName);

        if (classDoc != null && enumAnnotation != null) {
            for (MethodDoc methodDoc : classDoc.methods()) {
                for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
                    if (annotationDesc.annotationType().qualifiedTypeName().equals(enumAnnotation.getCanonicalName())) {
                        cc.catalysts.cdoclet.generator.Type type = GeneratorUtils.getType(methodDoc.returnType(), generator, new HashSet<String>());
                        enumerationTypes.put(typeName, type);
                        return type;
                    }
                }
            }

            enumerationTypes.put(typeName, cc.catalysts.cdoclet.generator.Type.EMPTY);
        }

        return cc.catalysts.cdoclet.generator.Type.EMPTY;
    }

    public static cc.catalysts.cdoclet.generator.Type getType(Type type, Generator generator, Collection<String> ignore) {
        return getType(type, generator, ignore, new HashSet<String>());
    }

    public static boolean isEnum(Type type) {
        return type.asClassDoc() != null && type.asClassDoc().isEnum();
    }

    public static int getDimensions(Type type) {
        return type.dimension().length() / 2;
    }

    private static void getTypeBounds(Type argument, Generator generator, Map<String, cc.catalysts.cdoclet.generator.Type> bounds, Collection<String> ignore, Collection<String> visited) {
        Type[] types = null;
        if (argument instanceof TypeVariable) {
            types = argument.asTypeVariable().bounds();
        } else if (argument instanceof WildcardType) {
            types = argument.asWildcardType().extendsBounds();
            bounds.put("?", getType(Object.class.getCanonicalName(), generator, generator.getTypeMap())); // default, will be overridden
        }
        if (types != null) {
            Type boundType = types.length > 0 ? types[0] : null;

            if (boundType != null) {
                cc.catalysts.cdoclet.generator.Type bound = getType(boundType, generator, ignore, visited);

                if (bound != cc.catalysts.cdoclet.generator.Type.NULL) {
                    logger.debug("Adding boundary {} extends {} <{}>", new Object[]{argument.qualifiedTypeName(), boundType.qualifiedTypeName(), boundType.getClass().getCanonicalName()});
                    bounds.put(argument.qualifiedTypeName(), bound);
                }
            }
        }
    }

    private static cc.catalysts.cdoclet.generator.Type getType(String name, Generator generator, TypeMap typeMap) {
        String type = typeMap.getType(name, false, false);
        if (type == null) return null;
        if ("null".equals(type)) return cc.catalysts.cdoclet.generator.Type.NULL;
        type = generator.getPackageMap().getType(type, false, false);
        return generator.postProcessType(new cc.catalysts.cdoclet.generator.Type(type, null, null, 0, false, generator.getClassMap()));
    }

    public static cc.catalysts.cdoclet.generator.Type getType(String name, Generator generator) {
        if (name == null) return cc.catalysts.cdoclet.generator.Type.NULL;
        return getType(name, generator, generator.getTypeMap());
    }

    public static cc.catalysts.cdoclet.generator.Type getAnnotationType(String name, Generator generator) {
        return getType(name, generator, generator.getAnnotationTypeMap());
    }

    public static cc.catalysts.cdoclet.generator.Type getAnnotation(String name, Generator generator) {
        return getType(name, generator, generator.getAnnotationMap());
    }
}
