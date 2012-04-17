package cc.catalysts.cdoclet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sun.tools.javadoc.Main;

@RunWith(Parameterized.class)
public abstract class AbstractGeneratorTest {
	private static final String inputDirectory = "src/test/java/cdoclet/test/";
	
	@Parameters
 	public static List<Object[]> data() {
 		List<Object[]> input = new ArrayList<Object[]>();
 		for (String file : new File(inputDirectory).list()) {
 			input.add(new Object[]{file});
 		}
 		return input;
 	}
 	
 	protected final String getOutputDir() {
 		return "test/results/" + getLanguage() + "/cdoclet/test/";
 	}
 	
 	protected abstract String getLanguage();
 	
	private final String inputFile;
 
 	public AbstractGeneratorTest(String inputFile) {
		this.inputFile = inputFile;
 	}
 	
	@Test
	public void testGeneration() throws IOException {
		String inputFile = inputDirectory + this.inputFile;
		String fileWithoutExt = this.inputFile.substring(0, this.inputFile.indexOf('.'));
		
		Main.execute(new String[]{"-d", "test/results/" + getLanguage(), "-doclet", "cc.catalysts.cdoclet.CDoclet", "-generator", getLanguage(), inputFile});
		
		File output = new File(getOutputDir() + fileWithoutExt + "." + getLanguage());
		
		File expected = new File("src/test/java/expectations/" + getLanguage()+ "/cdoclet/test/" + fileWithoutExt + "." + getLanguage());
		
		Assert.assertTrue("output file " + output.getAbsolutePath() + " does not exist", output.exists());
		Assert.assertTrue("file " + expected.getName() + " has not been generated", expected.exists());

		String outputString = FileUtils.readFileToString(output);
		String expectedString = FileUtils.readFileToString(expected);
		Assert.assertEquals(expected + " does not match", outputString, expectedString);
	}


}
