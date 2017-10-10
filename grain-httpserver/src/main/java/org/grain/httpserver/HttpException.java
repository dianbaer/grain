package org.grain.httpserver;

import com.google.protobuf.Message;

public final class HttpException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorType;
	private Message errorData;

	public Message getErrorData() {
		return errorData;
	}

	public void setErrorData(Message errorData) {
		this.errorData = errorData;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public HttpException(String errorType, Message errorData) {
		this.errorType = errorType;
		this.errorData = errorData;
	}
}
