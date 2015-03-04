package test;

/**
 * @java.class ignore=TestOverrideClassBase
 * @actionscript.class ignore=TestOverrideClassBase
 * @cs.class ignore=TestOverrideClassBase
 */
public class TestIgnoreInClassBase<T> extends test.TestOverrideClassBase<T> {
	@Override
	public T getTest() {
		return super.getTest();
	}
}
