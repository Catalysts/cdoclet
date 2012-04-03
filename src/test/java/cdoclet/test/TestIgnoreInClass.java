package cdoclet.test;

/**
 * @actionscript.class
 * @cs.class
 */
public class TestIgnoreInClass<T> extends TestIgnoreInClassBase<T> {
// --------------------- Interface TestOverrideInterfaceBase ---------------------

	@Override
	public T getTest() {
		return super.getTest();
	}

	@Override
	public void testMethod() {
	}
}
