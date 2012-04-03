

namespace cdoclet.test {
#region CDoclet generated code
	public class TestProxyInterfaceProxy : cdoclet.test.TestProxyInterface {
        protected virtual object DispatchCall(System.Type type, string methodName, params object[] args) {
            return null;
        }

        protected virtual void BeginDispatchCall(System.Type type, string methodName, object async, params object[] args) {
        }

		virtual public void Foo(AsyncCallback<System.Collections.Generic.ICollection<java.sql.Date>> async, java.sql.Date bar) {
			BeginDispatchCall(typeof(System.Collections.Generic.ICollection<java.sql.Date>), "foo", async, bar);
		}
		virtual public void Foo(AsyncCallback async, string bar) {
			BeginDispatchCall(typeof(object), "foo", async, bar);
		}
	}
#endregion
}