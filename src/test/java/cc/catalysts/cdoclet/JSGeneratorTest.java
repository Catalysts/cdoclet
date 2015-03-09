package cc.catalysts.cdoclet;

import cc.catalysts.cdoclet.generator.Languages;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;

@RunWith(Parameterized.class)
public class JSGeneratorTest extends AbstractGeneratorTest {

    public JSGeneratorTest(String inputFile) {
        super(inputFile);
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        FileUtils.deleteDirectory(new File(new JSGeneratorTest(null).getOutputDir()));
    }

    protected String getLanguage() {
        return Languages.JAVASCRIPT;
    }

    @Override
    protected String getOutputDir() {
        return "test/results/js/testjs/";
    }
}
