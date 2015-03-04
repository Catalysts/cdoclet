

namespace test {
#region CDoclet generated code
	public class TestProxyInterfaceProxy : test.TestProxyInterface {
        protected virtual object DispatchCall(System.Type type, string methodName, params object[] args) {
            return null;
        }

        protected virtual void BeginDispatchCall(System.Type type, string methodName, object async, params object[] args) {
        }

		virtual public void Baz(AsyncCallback async, long ago) {
			BeginDispatchCall(typeof(object), "baz", async, ago);
		}
	}
#endregion
}