package dbtest.exception;

public class FieldIsNoBeanSqlPropertyException extends ClassIsNoSqlBeanReasonException {

	private static final long serialVersionUID = 3857942082073155018L;

	public FieldIsNoBeanSqlPropertyException() {
		super();
	}

	public FieldIsNoBeanSqlPropertyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldIsNoBeanSqlPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldIsNoBeanSqlPropertyException(String message) {
		super(message);
	}

	public FieldIsNoBeanSqlPropertyException(Throwable cause) {
		super(cause);
	}
	
}