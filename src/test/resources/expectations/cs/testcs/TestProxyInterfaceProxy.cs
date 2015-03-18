

namespace testcs {
#region CDoclet generated code
	public class TestProxyInterfaceProxy : testcs.TestProxyInterface {
        protected virtual object DispatchCall(System.Type type, string methodName, params object[] args) {
            return null;
        }

        protected virtual void BeginDispatchCall(System.Type type, string methodName, object async, params object[] args) {
        }

		virtual public void Baz(AsyncCallback<testcs.TestBeanDto> async, long ago, string value) {
			BeginDispatchCall(typeof(testcs.TestBeanDto), "baz", async, ago, value);
		}
	}
#endregion
}
