package cc.catalysts.cdoclet;

import com.sun.tools.javadoc.Main;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(Parameterized.class)
public abstract class AbstractGeneratorTest {
    private static final String inputDirectory = "src/test/resources/test/";

    @Parameters(name = "{0}")
    public static List<Object[]> data() {
        List<Object[]> input = new ArrayList<Object[]>();
        for (String file : new File(inputDirectory).list()) {
            input.add(new Object[]{file});
        }
        return input;
    }

    protected abstract String getOutputDir();

    protected abstract String getLanguage();

    private final String inputFile;

    public AbstractGeneratorTest(String inputFile) {
        this.inputFile = inputFile;
    }

    @Test
    public void testGeneration() throws IOException {
        String inputFile = inputDirectory + this.inputFile;
        String fileWithoutExt = this.inputFile.substring(0, this.inputFile.indexOf('.'));

        String fileExt = getLanguage();
        if (fileExt.equals(cc.catalysts.cdoclet.generator.Languages.ACTIONSCRIPT)) {
            fileExt = "as";
        }

        int result = Main.execute(new String[]{"-d", "test/results/" + fileExt, "-doclet", "cc.catalysts.cdoclet.CDoclet", "-generator", getLanguage(), "-packagemap", "test:test" + getLanguage(), "-suffix", "Dto", inputFile});

        if (result > 0) {
            Assert.fail("javadoc error");
        }

        File output = new File(getOutputDir() + fileWithoutExt + "." + fileExt);

        File expected = new File("src/test/resources/expectations/" + fileExt + "/test/" + fileWithoutExt + "." + fileExt);

        if (!expected.exists()) {
            System.err.println("file " + expected.getAbsolutePath() + " does not exist\nSkipping Test!");
            return;
        }
        Assert.assertTrue("output file " + output.getAbsolutePath() + " has not been generated", output.exists());

        String outputString = FileUtils.readFileToString(output);
        String expectedString = FileUtils.readFileToString(expected);
        //Assert.assertEquals(expected + " does not match in length", expectedString.length(), outputString.length());
        Assert.assertEquals(expected + " does not match", expectedString, outputString);
    }


}
