package http.exception;

import com.google.protobuf.Message;

public final class HttpErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	private int errorType;
	private Message errorData;

	public Message getErrorData() {
		return errorData;
	}

	public void setErrorData(Message errorData) {
		this.errorData = errorData;
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	public HttpErrorException(int errorType, Message errorData) {
		this.errorType = errorType;
		this.errorData = errorData;
	}
}
