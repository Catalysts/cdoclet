package cdoclet.test;

/**
 * @java.class
 * @as.class annotation=Bindable
 * @cs.class annotation=System.Serializable
 */
@Deprecated
public interface TestInterfaceAnnotations {
	/**
	 * @as.method annotation=Bindable
	 * @cs.method annotation=System.Serializable
	 */
	@Deprecated
	void annotatedMethod();

	void notAnnotatedMethod();
}
