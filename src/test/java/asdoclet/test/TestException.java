package asdoclet.test;

/**
 * @actionscript.class
 * @cs.class
 */
public class TestException extends Exception {
	private static final long serialVersionUID = 1L;

	public TestException() {
	}

	public TestException(String message) {
		super(message);
	}

	public TestException(Throwable cause) {
		super(cause);
	}

	public TestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @return message
	 * @cs.property ignore=<code>true</code>
	 */
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
