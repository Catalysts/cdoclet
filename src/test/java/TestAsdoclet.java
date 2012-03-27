import com.sun.tools.javadoc.Main;

public class TestAsdoclet {
	public static void main(String[] args) {
		Main.execute(new String[]{"-d", "test/results/cs", "-doclet", "org.fluffnstuff.asdoclet.AsDoclet", "-generator", "cs", "asdoclet.test", "test/asdoclet/test/TestProxyInterface.java"});
	}
}
