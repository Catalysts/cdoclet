package cc.catalysts.cdoclet;

import cc.catalysts.cdoclet.generator.AsGenerator;
import cc.catalysts.cdoclet.generator.Generator;
import cc.catalysts.cdoclet.generator.Languages;
import cc.catalysts.cdoclet.generator.TemplateGenerator;
import cc.catalysts.cdoclet.handler.*;
import cc.catalysts.cdoclet.map.ClassTypeMap;
import cc.catalysts.cdoclet.map.PackageTypeMap;
import cc.catalysts.cdoclet.map.TypeMap;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Catalysts Gmbh
 */
public class CDoclet {
    private static final String PARAM_DESTINATION = "-d";
    private static final String PARAM_ENCODING = "-encoding";
    private static final String PARAM_NAMESPACE = "-namespace";
    private static final String PARAM_GENERATOR = "-generator";
    private static final String PARAM_ENUM = "-enum";
    private static final String PARAM_SUFFIX = "-suffix";
    private static final String PARAM_MAP = "-map";
    private static final String PARAM_PACKAGE_MAP = "-packagemap";
    private static final String PARAM_GENERIC_MAP = "-genericmap";
    private static final String PARAM_ANNOTATION_MAP = "-annotation";
    private static final String PARAM_ANNOTATION_TYPE_MAP = "-annotationmap";

    private static final Logger logger = LoggerFactory.getLogger(CDoclet.class);

    static {
        try {
            URL config = CDoclet.class.getResource("logback-test.xml");
            if (config == null) config = CDoclet.class.getResource("logback.xml");

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            lc.reset();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            configurator.doConfigure(config);

            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean start(RootDoc root) throws Exception {
        Generator generator = readOptions(root.options());

        ClassDoc[] docs = root.specifiedClasses();
        if (docs.length == 0) docs = root.classes();

        for (ClassDoc classDoc : docs) {
            logger.info("Processing {}", classDoc.qualifiedName());

            if (TagParser.hasClassTags(generator, classDoc)) {
                Handler handler = createHandler(generator, classDoc);
                handler.process(classDoc);
            }
        }

        generator.generate();

        return true;
    }

    @SuppressWarnings({"unchecked"})
    private static Generator readOptions(String[][] options) throws Exception {
        String destination = ".";
        String encoding = "UTF-8";
        String namespace = null;
        String generator = null;
        String suffix = null;
        Class<? extends Annotation> enumAnnotation = null;

        ClassTypeMap typeMap = new ClassTypeMap();
        PackageTypeMap packageMap = new PackageTypeMap();
        TypeMap annotationMap = new TypeMap();
        TypeMap annotationTypeMap = new TypeMap();

        for (String[] opt : options) {
            if (opt[0].equals(PARAM_DESTINATION)) {
                destination = opt[1];
            } else if (opt[0].equals(PARAM_ENCODING)) {
                encoding = opt[1];
            } else if (opt[0].equals(PARAM_NAMESPACE)) {
                namespace = opt[1];
            } else if (opt[0].equals(PARAM_GENERATOR)) {
                generator = opt[1];
            } else if (opt[0].equals(PARAM_SUFFIX)) {
                suffix = opt[1];
            } else if (opt[0].equals(PARAM_ENUM)) {
                enumAnnotation = (Class<? extends Annotation>) Class.forName(opt[1]);
            } else if (opt[0].equals(PARAM_MAP)) {
                String[] strings = opt[1].split(":");
                if (strings.length == 2) typeMap.addTypeMapping(strings[0], strings[1]);
            } else if (opt[0].equals(PARAM_PACKAGE_MAP)) {
                String[] strings = opt[1].split(":");
                if (strings.length == 2) packageMap.addTypeMapping(strings[0], strings[1]);
            } else if (opt[0].equals(PARAM_GENERIC_MAP)) {
                String[] strings = opt[1].split(":");
                if (strings.length == 2) typeMap.addGenericTypeMapping(strings[0], strings[1]);
            } else if (opt[0].equals(PARAM_ANNOTATION_MAP)) {
                String[] strings = opt[1].split(":");
                if (strings.length == 2) annotationMap.addTypeMapping(strings[0], strings[1]);
            } else if (opt[0].equals(PARAM_ANNOTATION_TYPE_MAP)) {
                String[] strings = opt[1].split(":");
                if (strings.length == 2) annotationTypeMap.addTypeMapping(strings[0], strings[1]);
            }
        }

        if (suffix == null) {
            suffix = "";
        }

        if (Languages.ACTIONSCRIPT.equals(generator)) {
            return new AsGenerator(destination, namespace, suffix, enumAnnotation, typeMap, packageMap, annotationTypeMap, annotationMap);
        } else if (Languages.CSHARP.equals(generator) || Languages.JAVA.equals(generator) || Languages.JAVASCRIPT.equals(generator)) {
            return new TemplateGenerator(destination, encoding, namespace, generator, suffix, enumAnnotation, typeMap, packageMap, annotationTypeMap, annotationMap);
        }

        return null;
    }

    private static Handler createHandler(Generator generator, ClassDoc classDoc) {
        Handler handler;

        if (classDoc.isInterface()) {
            handler = new InterfaceHandler(generator);
        } else if (classDoc.isEnum()) {
            handler = new EnumHandler(generator);
        } else {
            handler = new ClassHandler(generator);
        }

        return handler;
    }

    public static int optionLength(String option) {
        int length = 0;

        if (Arrays.asList(
                PARAM_DESTINATION,
                PARAM_NAMESPACE,
                PARAM_GENERATOR,
                PARAM_ENUM,
                PARAM_SUFFIX,
                PARAM_MAP,
                PARAM_PACKAGE_MAP,
                PARAM_GENERIC_MAP,
                PARAM_ANNOTATION_MAP,
                PARAM_ANNOTATION_TYPE_MAP
        ).contains(option)) length = 2;

        return length;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        // todo print pretty usage message
        return true;
    }

    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    private CDoclet() {
    }
}
