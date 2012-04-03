package cdoclet.test;

/**
 * @actionscript.class ignore=cdoclet.test.TestOverrideClassBase
 * @cs.class ignore=cdoclet.test.TestOverrideClassBase
 */
public class TestIgnoreClass<T> extends TestOverrideClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}