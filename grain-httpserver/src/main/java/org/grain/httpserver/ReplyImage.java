package org.grain.httpserver;

import java.io.File;

public class ReplyImage {
	private File file;
	private String contentType = "image/jpeg";

	public ReplyImage(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
