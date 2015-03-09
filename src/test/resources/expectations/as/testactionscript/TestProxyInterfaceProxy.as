package  testactionscript{
	import testactionscript.TestBean;
	import testactionscript.TestProxyInterface;
	import testactionscript.TestProxyInterfaceBase;
	public class TestProxyInterfaceProxy implements testactionscript.TestProxyInterface {
		protected function dispatchCall(name:String, ...args):Object {
			throw new Error("Not Implemented");
		}
		protected function onResult(result:Object):void {
		}
		protected function onStatus(status:Object):void {
		}
		public function baz(ago:Number):testactionscript.TestBean {
			return dispatchCall(TestProxyInterfaceProxyEvents.Baz, ago) as testactionscript.TestBean;
		}
		public function foo(bar:Date):Array {
			return dispatchCall(TestProxyInterfaceProxyEvents.Foo, bar) as Array;
		}
	}
}