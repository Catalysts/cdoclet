import com.sun.tools.javadoc.Main;

public class TestCDoclet {
	public static void main(String[] args) {
		Main.execute(new String[]{"-d", "test/results/cs", "-doclet", "cc.catalysts.cdoclet.CDoclet", "-generator", "cs", "src/test/java/cdoclet/test/TestProxyInterface.java"});
	}
}
