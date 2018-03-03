package org.grain.httpserver;

public class ReplyString {
	private String str;
	private String contentType;
	public ReplyString(String str,String contentType) {
		this.str = str;
		this.contentType = contentType;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
