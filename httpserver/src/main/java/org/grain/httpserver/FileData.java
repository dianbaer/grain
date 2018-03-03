package org.grain.httpserver;

import java.io.File;

public class FileData {
	/**
	 * 上传文件
	 */
	private File file;
	/**
	 * 上传文件名
	 */
	private String fileName;

	public FileData(File file, String fileName) {
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
