package  test{
	import test.TestProxyInterface;
	import test.TestProxyInterfaceBase;
	public class TestProxyInterfaceProxy implements test.TestProxyInterface {
		protected function dispatchCall(name:String, ...args):Object {
			throw new Error("Not Implemented");
		}
		protected function onResult(result:Object):void {
		}
		protected function onStatus(status:Object):void {
		}
		public function baz(ago:Number):void {
			dispatchCall(TestProxyInterfaceProxyEvents.Baz, ago);
		}
		public function foo(bar:Date):Array {
			return dispatchCall(TestProxyInterfaceProxyEvents.Foo, bar) as Array;
		}
	}
}