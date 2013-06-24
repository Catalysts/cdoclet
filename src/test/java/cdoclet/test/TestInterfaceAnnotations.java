package cdoclet.test;

/**
 * @java.class
 * @actionscript.class annotation=Bindable
 * @cs.class annotation=System.Serializable
 */
@Deprecated
public interface TestInterfaceAnnotations {
	/**
	 * @actionscript.method annotation=Bindable
	 * @cs.method annotation=System.Serializable
	 */
	@Deprecated
	void annotatedMethod();

	void notAnnotatedMethod();
}
