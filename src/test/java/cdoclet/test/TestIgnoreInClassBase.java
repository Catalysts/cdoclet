package cdoclet.test;

 /**
 * @java.class ignore=cdoclet.test.TestOverrideClassBase
 * @as.class ignore=cdoclet.test.TestOverrideClassBase
 * @cs.class ignore=cdoclet.test.TestOverrideClassBase
 */
public class TestIgnoreInClassBase<T> extends TestOverrideClassBase<T> {
	@Override
	public T getTest() {
		return super.getTest();
	}
}
