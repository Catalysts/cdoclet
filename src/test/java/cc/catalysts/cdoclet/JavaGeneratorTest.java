package cc.catalysts.cdoclet;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import cc.catalysts.cdoclet.generator.Languages;

@RunWith(Parameterized.class)
public class JavaGeneratorTest extends AbstractGeneratorTest {

	public JavaGeneratorTest(String inputFile) {
		super(inputFile);
	}
	
 	@BeforeClass
 	public static void beforeClass() throws IOException {
 		FileUtils.deleteDirectory(new File(new JavaGeneratorTest(null).getOutputDir()));
 	}
 	
 	protected String getLanguage() {
 		return Languages.JAVA;
 	}

    @Override
    protected String getOutputDir() {
        return "test/results/java/cdoclet/test/";
    }
}
