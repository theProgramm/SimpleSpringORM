package dbtest.exception;

public class ClassIsNoSqlBeanReasonException extends Exception{

	private static final long serialVersionUID = -6129374324294479910L;

	public ClassIsNoSqlBeanReasonException() {
		super();
	}

	public ClassIsNoSqlBeanReasonException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ClassIsNoSqlBeanReasonException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClassIsNoSqlBeanReasonException(String message) {
		super(message);
	}

	public ClassIsNoSqlBeanReasonException(Throwable cause) {
		super(cause);
	}
	
}