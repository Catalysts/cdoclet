package cdoclet.test;

/**
 * @java.class
 * @actionscript.class
 * @cs.class
 */
public class TestOverrideClass<T> extends TestOverrideClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}
