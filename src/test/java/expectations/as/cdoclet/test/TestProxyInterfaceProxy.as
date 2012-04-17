package  cdoclet.test{
	import cdoclet.test.TestProxyInterface;
	import cdoclet.test.TestProxyInterfaceBase;
	public class TestProxyInterfaceProxy implements cdoclet.test.TestProxyInterface {
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