package model.exceptions;

public class TokenException extends Exception {

	private static final long serialVersionUID = 1L;

	public TokenException(String message, Exception cause) {
		super(message, cause);
	}

}