package org.grain.threadkeylock;

public class KeyLockException extends Exception {

	private static final long serialVersionUID = 1L;

	public KeyLockException(String message) {
		super(message);
	}
}
