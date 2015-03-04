import com.sun.tools.javadoc.Main;

public class TestCDoclet {
	public static void main(String[] args) {
		Main.execute(new String[]{"-d", "test/results/cs", "-doclet", "cc.catalysts.cdoclet.CDoclet", "-generator", "cs", "src/test/resources/test/TestProxyInterface.java"});
		Main.execute(new String[]{"-d", "test/results/java", "-doclet", "cc.catalysts.cdoclet.CDoclet", "-generator", "java", "src/test/resources/test/TestProxyInterface.java"});
	}
}
