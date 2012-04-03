package cc.catalysts.cdoclet.handler;

final public class Constants {
	public static final String TAG_CLASS = ".class";
	public static final String TAG_METHOD = ".method";
	public static final String TAG_PROPERTY = ".property";

	public static final String COMMAND_BEAN = "bean";

	public static final String COMMAND_ASYNC = "async";
	public static final String COMMAND_ASYNC_RETURN_TYPE = "async.returntype";
	public static final String COMMAND_ASYNC_CALLBACK_TYPE = "async.callbacktype";

	public static final String COMMAND_PROXY = "proxy";
	public static final String COMMAND_PROXY_SUPER_CLASS = "proxy.superclass";
	public static final String COMMAND_PROXY_NAME = "proxy.name";

	public static final String COMMAND_ANNOTATION = "annotation";
	public static final String COMMAND_IGNORE = "ignore";
	public static final String COMMAND_TYPE = "type";

	public static final String METHOD_ON_RESULT = "onResult";
	public static final String METHOD_ON_STATUS = "onStatus";
	public static final String METHOD_CALL = "dispatchCall";

	public static final String PARAM_ASYNC = "async";

	private Constants() {
	}
}
