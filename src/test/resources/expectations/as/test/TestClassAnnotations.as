package  test{
	/**
	 * Generated by CDoclet from test.TestClassAnnotations. Do not edit.
	 */
	[RemoteClass(alias="test.TestClassAnnotations")]
	[Deprecated]
	[Bindable]
	public class TestClassAnnotations {
		private var _annotatedProperty:String;
		[Deprecated]
		[Bindable]
		public function get annotatedProperty():String {
			return _annotatedProperty;
		}
		[Deprecated]
		[Bindable]
		public function set annotatedProperty(value:String):void {
			_annotatedProperty=value;
		}
		private var _notAnnotatedProperty:String;
		public function get notAnnotatedProperty():String {
			return _notAnnotatedProperty;
		}
		public function set notAnnotatedProperty(value:String):void {
			_notAnnotatedProperty=value;
		}
	}
}