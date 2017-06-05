package http.filter;

import http.exception.HttpErrorException;

public interface IHttpInitFilter extends IHttpFilter{
	public void httpInit() throws HttpErrorException;
}
