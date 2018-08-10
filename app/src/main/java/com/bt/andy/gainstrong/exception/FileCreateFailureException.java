package com.bt.andy.gainstrong.exception;

/**
 * @author AndyYan
 */
public class FileCreateFailureException extends Exception {
	private static final long serialVersionUID = -5076198159595208735L;

	public FileCreateFailureException() {}
	
	public FileCreateFailureException(String message) {
		super(message);
	}
	
	public FileCreateFailureException(Throwable throwable) {
		super(throwable);
	}
	
	public FileCreateFailureException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
