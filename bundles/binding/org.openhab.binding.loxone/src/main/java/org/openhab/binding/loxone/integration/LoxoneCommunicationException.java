package org.openhab.binding.loxone.integration;

public class LoxoneCommunicationException extends RuntimeException{
	private static final long serialVersionUID = 1039792928609854756L;

	public LoxoneCommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoxoneCommunicationException(String message) {
		super(message);
	}
	

}
