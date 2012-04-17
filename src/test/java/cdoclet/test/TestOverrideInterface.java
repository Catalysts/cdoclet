package cdoclet.test;

/**
 * @java.class
 * @as.class
 * @cs.class
 */
public interface TestOverrideInterface<T> extends TestOverrideInterfaceBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	public T getTest();

	void testMethod();
}
