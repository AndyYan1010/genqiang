package com.bt.andy.gainstrong.exception;

/**
 * @author AndyYan
 */
public class SdCardNotValidException extends Exception {
	private static final long serialVersionUID = -5499911517665209427L;

	public SdCardNotValidException() {}
	
	public SdCardNotValidException(String message) {
		super(message);
	}
	
	public SdCardNotValidException(Throwable throwable) {
		super(throwable);
	}
	
	public SdCardNotValidException(String message, Throwable throwable) {
		super(message, throwable);
	}
} 
