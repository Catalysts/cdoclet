

package testjava;
	public class TestProxyInterfaceProxy implements testjava.TestProxyInterface {
        protected virtual object DispatchCall(System.Type type, string methodName, params object[] args) {
            return null;
        }

        protected virtual void BeginDispatchCall(System.Type type, string methodName, object async, params object[] args) {
        }

		
	public testjava.TestBeanDto baz(long ago) {
			return (<testjava.TestBeanDto>) DispatchCall(typeof<testjava.TestBeanDto), "baz", ago);
		}
	}
}
