package dbtest.exception;

import java.util.Arrays;


public class ClassIsNoSqlBeanException extends Exception {
	
	private static final long serialVersionUID = 1200029052817087442L;
	private final ClassIsNoSqlBeanReasonException[] reasons;

	public ClassIsNoSqlBeanException(ClassIsNoSqlBeanReasonException... reasons) {
		super();
		this.reasons = reasons;
	}

	public ClassIsNoSqlBeanException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace,
			ClassIsNoSqlBeanReasonException... reasons) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.reasons = reasons;
	}

	public ClassIsNoSqlBeanException(String message, Throwable cause,
			ClassIsNoSqlBeanReasonException... reasons) {
		super(message, cause);
		this.reasons = reasons;
	}

	public ClassIsNoSqlBeanException(String message,
			ClassIsNoSqlBeanReasonException... reasons) {
		super(message);
		this.reasons = reasons;
	}

	public ClassIsNoSqlBeanException(Throwable cause,
			ClassIsNoSqlBeanReasonException... reasons) {
		super(cause);
		this.reasons = reasons;
	}

	@Override
	public String toString() {
		return "ClassIsNoSqlBeanException [reasons=" + Arrays.toString(reasons) + "]";
	}

	public ClassIsNoSqlBeanReasonException[] getReasons() {
		return reasons;
	}
	
}