package cc.catalysts.cdoclet.handler;

import cc.catalysts.cdoclet.generator.Generator;
import cc.catalysts.cdoclet.generator.utils.GeneratorUtils;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

import java.util.*;

public class InterfaceHandler extends AbstractHandler {
    public InterfaceHandler(Generator generator) {
        super(generator);
    }

// --------------------- Interface Handler ---------------------

    public void process(ClassDoc classDoc) {
        Map<String, String> commands = TagParser.processClassTags(getGenerator(), classDoc);

        boolean proxy = TagParser.getBooleanCommand(Constants.COMMAND_PROXY, commands);
        if (proxy) {
            cc.catalysts.cdoclet.generator.Type interfaceName = GeneratorUtils.getType(classDoc, getGenerator(), new HashSet<String>());
            String proxyName = TagParser.getStringCommand(Constants.COMMAND_PROXY_NAME, interfaceName + "Proxy", commands);
            String baseType = TagParser.getStringCommand(Constants.COMMAND_PROXY_SUPER_CLASS, commands);

            cc.catalysts.cdoclet.generator.Type proxyType = GeneratorUtils.getType(proxyName, getGenerator());
            cc.catalysts.cdoclet.generator.Type proxyBaseType = GeneratorUtils.getType(baseType, getGenerator());
            getGenerator().beginProxy(proxyType, proxyBaseType, interfaceName);
        }

        processInterfaceInternal(classDoc, commands);

        if (proxy) {
            Set<String> ignore = getIgnore(commands);

            traverseInterfaces(classDoc, ignore, commands);
            getGenerator().endProxy();
        }
    }

    private void processInterfaceInternal(ClassDoc classDoc, Map<String, String> commands) {
        boolean bean = TagParser.getBooleanCommand(Constants.COMMAND_BEAN, commands);
        Set<String> ignore = getIgnore(commands);

        cc.catalysts.cdoclet.generator.Type type = GeneratorUtils.getType(classDoc, getGenerator(), ignore);

        getGenerator().beginInterface(type);

        processAnnotations(classDoc.annotations());
        processAnnotationCommands(commands);
        processInterfaces(classDoc, ignore);
        processClassComment(classDoc);
        processClassConstants(type, classDoc, ignore);
        processMethods(classDoc, type, bean, ignore, commands, null);

        getGenerator().endInterface();
    }

    private void processMethods(ClassDoc classDoc, cc.catalysts.cdoclet.generator.Type classType, boolean bean, Set<String> ignore, Map<String, String> commands, Map<String, cc.catalysts.cdoclet.generator.Type> typeMap) {
        for (MethodDoc methodDoc : classDoc.methods()) {
            boolean getter = (methodDoc.name().startsWith("get") || methodDoc.name().startsWith("is")) && (methodDoc.parameters().length == 0);
            boolean setter = methodDoc.name().startsWith("set") && (methodDoc.parameters().length == 1);

            if (bean && (getter || setter)) {
                processBeanProperty(classDoc, classType, methodDoc, ignore, commands);
            } else {
                processMethod(classDoc, classType, methodDoc, ignore, commands, typeMap);
            }
        }
    }

    private void processMethod(ClassDoc classDoc, cc.catalysts.cdoclet.generator.Type classType, MethodDoc methodDoc, Set<String> ignore, Map<String, String> commands, Map<String, cc.catalysts.cdoclet.generator.Type> typeMap) {
        Map<String, String> methodCommands = new HashMap<String, String>(commands);
        Map<String, String> methodOnlyCommands = new HashMap<String, String>();

        cc.catalysts.cdoclet.generator.Type returnType = GeneratorUtils.getType(methodDoc.returnType(), getGenerator(), ignore);
        cc.catalysts.cdoclet.generator.Type methodType = GeneratorUtils.getType(methodDoc, getGenerator(), ignore);

        if (typeMap != null) {
            if (typeMap.containsKey(returnType.getName())) returnType = typeMap.get(returnType.getName());
            if (typeMap.containsKey(methodType.getName())) methodType = typeMap.get(methodType.getName());
        }

        returnType.setTypeMap(typeMap);

        cc.catalysts.cdoclet.generator.Type actualReturnType = returnType;

        TagParser.processTags(methodDoc.tags(getGenerator().getName() + Constants.TAG_METHOD), methodOnlyCommands);
        methodCommands.putAll(methodOnlyCommands);

        if (!TagParser.getBooleanCommand(Constants.COMMAND_IGNORE, methodCommands)) {
            cc.catalysts.cdoclet.generator.Type asyncType = cc.catalysts.cdoclet.generator.Type.EMPTY;
            String verb = TagParser.getStringCommand(Constants.COMMAND_VERB, "GET", methodCommands);
            boolean async = TagParser.getBooleanCommand(Constants.COMMAND_ASYNC, methodCommands);

            if (async) {
                String methodReturnType = TagParser.getStringCommand(Constants.COMMAND_ASYNC_RETURN_TYPE, methodCommands);
                String callbackType = TagParser.getStringCommand(Constants.COMMAND_ASYNC_CALLBACK_TYPE, methodCommands);

                if (callbackType != null) {
                    if (callbackType.endsWith("<$type$>")) {
                        ArrayList<cc.catalysts.cdoclet.generator.Type> arguments = new ArrayList<cc.catalysts.cdoclet.generator.Type>();
                        if (!cc.catalysts.cdoclet.generator.Type.VOID.getQualifiedTypeName().equals(returnType.getQualifiedTypeName()))
                            arguments.add(returnType);

                        asyncType = new cc.catalysts.cdoclet.generator.Type(callbackType.substring(0, callbackType.length() - 8), arguments, null, 0, false, false, getGenerator().getClassMap());
                    } else {
                        asyncType = new cc.catalysts.cdoclet.generator.Type(callbackType, null, null, 0, false, false, getGenerator().getClassMap());
                    }

                    asyncType.setTypeMap(typeMap);
                }

                actualReturnType = methodReturnType != null ? GeneratorUtils.getType(methodReturnType, getGenerator()) : cc.catalysts.cdoclet.generator.Type.VOID;
            }

            boolean isasync = asyncType != cc.catalysts.cdoclet.generator.Type.EMPTY;

            String name = methodDoc.name();
            boolean override = isOverridden(classDoc, name, ignore);
            getGenerator().beginMethod(classType, methodType, 0, actualReturnType, name, isasync, override, verb);

            processAnnotations(methodDoc.annotations());
            processAnnotationCommands(methodOnlyCommands);

            if (isasync) {
                getGenerator().addParameter(classType, methodType, asyncType, Constants.PARAM_ASYNC);
            }

            for (Parameter parameter : methodDoc.parameters()) {
                cc.catalysts.cdoclet.generator.Type type = GeneratorUtils.getType(parameter.type(), getGenerator(), ignore);
                type.setTypeMap(typeMap);
                getGenerator().addParameter(classType, methodType, type, parameter.name());
            }

            processMethodComment(methodDoc);

            getGenerator().endMethod(returnType);
        }
    }

    private void processMethodComment(MethodDoc methodDoc) {
        StringBuilder description = new StringBuilder();
        String comment = methodDoc.commentText();
        // todo maybe add exceptions to comment: Type[] exceptionTypes = methodDoc.thrownExceptionTypes();

        if (comment != null) description.append(comment.trim());

        getGenerator().setMethodDescription(description.toString());
    }

    private void traverseInterfaces(ClassDoc interfaceDoc, Set<String> ignore, Map<String, String> parentCommands) {
        for (Type type : interfaceDoc.interfaceTypes()) {
            ClassDoc classDoc = type.asClassDoc();

            // don't process interfaces that are not part of the source path
            if (!getGenerator().traverse(classDoc)) continue;

            cc.catalysts.cdoclet.generator.Type interfaceType = GeneratorUtils.getType(type, getGenerator(), ignore);

            if (interfaceType != cc.catalysts.cdoclet.generator.Type.NULL) {
                Map<String, String> commands = new HashMap<String, String>(parentCommands);
                commands.putAll(TagParser.processClassTags(getGenerator(), classDoc));

                // add ignores from interfaces
                ignore = getIgnore(commands);

                cc.catalysts.cdoclet.generator.Type interfaceClass = GeneratorUtils.getType(classDoc, getGenerator(), ignore);
                Map<String, cc.catalysts.cdoclet.generator.Type> typeMap = null;

                if (interfaceClass.getArguments() != null && interfaceType.getArguments() != null) {
                    typeMap = new HashMap<String, cc.catalysts.cdoclet.generator.Type>();

                    Iterator<cc.catalysts.cdoclet.generator.Type> classIterator = interfaceClass.getArguments().iterator();
                    Iterator<cc.catalysts.cdoclet.generator.Type> typeIterator = interfaceType.getArguments().iterator();
                    while (classIterator.hasNext() && typeIterator.hasNext()) {
                        typeMap.put(classIterator.next().getName(), typeIterator.next());
                    }
                }

                boolean bean = TagParser.getBooleanCommand(Constants.COMMAND_BEAN, commands);
                processMethods(classDoc, interfaceClass, bean, ignore, commands, typeMap);

                traverseInterfaces(classDoc, ignore, commands);
            }
        }
    }
}
