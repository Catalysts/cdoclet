package cdoclet.test;

/**
 * @java.class
 * @as.class annotation=Bindable
 * @cs.class annotation=System.Serializable
 */
@Deprecated
public class TestClassAnnotations {
	/**
	 * @return <code>null</code>
	 * @actionscript.property annotation=Bindable
	 * @cs.property annotation=System.Serializable
	 */
	@Deprecated
	public String getAnnotatedProperty() {
		return null;
	}

	public String getNotAnnotatedProperty() {
		return null;
	}
}
