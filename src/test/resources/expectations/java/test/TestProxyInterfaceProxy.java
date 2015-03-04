

package test;
	public class TestProxyInterfaceProxy implements test.TestProxyInterface {
        protected virtual object DispatchCall(System.Type type, string methodName, params object[] args) {
            return null;
        }

        protected virtual void BeginDispatchCall(System.Type type, string methodName, object async, params object[] args) {
        }

		
	public void baz(long ago) {
			DispatchCall(typeof(object), "baz", ago);
		}
	}
}