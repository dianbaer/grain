package org.grain.httpserver;

import java.io.File;

public class ReplyFile {
	private String fileName;
	private File file;

	public ReplyFile(File file, String fileName) {
		this.file = file;
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
