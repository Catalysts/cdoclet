package  test{
	import test.TestOverrideClassBase;
	/**
	 * Generated by CDoclet from test.TestIgnoreClass.
	 */
	[RemoteClass(alias="test.TestIgnoreClass")]
	public class TestIgnoreClass extends test.TestOverrideClassBase  {
		private var _test:Object;
		public override function get test():Object {
			return _test;
		}
		public override function set test(value:Object):void {
			_test=value;
		}
	}
}