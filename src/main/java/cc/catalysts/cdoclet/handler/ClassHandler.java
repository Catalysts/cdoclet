package cc.catalysts.cdoclet.handler;

import cc.catalysts.cdoclet.generator.Generator;
import cc.catalysts.cdoclet.generator.utils.GeneratorUtils;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ClassHandler extends AbstractHandler {
    public ClassHandler(Generator generator) {
        super(generator);
    }

// --------------------- Interface Handler ---------------------

    public void process(ClassDoc classDoc) {
        Map<String, String> commands = TagParser.processClassTags(getGenerator(), classDoc);

        Set<String> ignore = getIgnore(commands);

        cc.catalysts.cdoclet.generator.Type type = GeneratorUtils.getType(classDoc, getGenerator(), ignore);

        getGenerator().beginClass(type);

        processAnnotations(classDoc.annotations());
        processAnnotationCommands(commands);
        processSuperCommands(commands);
        processSuperClass(classDoc, ignore);
        processInterfaces(classDoc, ignore);
        processClassComment(classDoc);
        processClassConstants(type, classDoc, ignore);
        processBeanProperties(type, classDoc, ignore, commands);

        getGenerator().endClass();
    }

    private void processBeanProperties(cc.catalysts.cdoclet.generator.Type type, ClassDoc classDoc, Set<String> ignore, Map<String, String> commands) {
        for (MethodDoc methodDoc : classDoc.methods()) {
            processBeanProperty(classDoc, type, methodDoc, ignore, commands);
        }
    }

    private void processSuperClass(ClassDoc classDoc, Collection<String> ignore) {
        Type type = classDoc.superclassType();
        if ((type != null) && !type.qualifiedTypeName().equals(Object.class.getCanonicalName())) {
            cc.catalysts.cdoclet.generator.Type superClass = GeneratorUtils.getType(type, getGenerator(), ignore);

            if (superClass != cc.catalysts.cdoclet.generator.Type.NULL && !ignore.contains(superClass.getName())) {
                getGenerator().setSuperclass(superClass, isException(classDoc));
            }
        }
    }

    private boolean isException(ClassDoc classDoc) {
        while (classDoc != null) {
            if (Exception.class.getCanonicalName().equals(classDoc.qualifiedTypeName())) return true;
            classDoc = classDoc.superclass();
        }

        return false;
    }
}
