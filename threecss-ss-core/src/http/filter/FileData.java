package http.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import log.LogManager;

public class FileData {
	private InputStream stream;
	private File file;
	private URL url;
	private String fileName;
	private String contentType;

	public FileData(InputStream stream, String fileName) {
		this.stream = stream;
		this.fileName = fileName;
	}

	public FileData(InputStream stream, String fileName, String contentType) {
		this(stream, fileName);
		this.contentType = contentType;
	}

	public FileData(File file, String fileName) {
		this.file = file;
		this.fileName = fileName;
	}

	public FileData(URL url, String fileName) {
		this.url = url;
		try {
			this.stream = url.openStream();
		} catch (IOException e) {
			LogManager.httpLog.error("未找到文件", e);
		}
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
