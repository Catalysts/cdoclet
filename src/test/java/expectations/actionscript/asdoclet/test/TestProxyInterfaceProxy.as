package  asdoclet.test{
	import asdoclet.test.TestProxyInterface;
	import asdoclet.test.TestProxyInterfaceBase;
	public class TestProxyInterfaceProxy implements asdoclet.test.TestProxyInterface {
		protected function dispatchCall(name:String, ...args):Object {
			throw new Error("Not Implemented");
		}
		protected function onResult(result:Object):void {
		}
		protected function onStatus(status:Object):void {
		}
		public function foo(bar:Date):Array {
			return dispatchCall(TestProxyInterfaceProxyEvents.Foo, bar) as Array;
		}
	}
}