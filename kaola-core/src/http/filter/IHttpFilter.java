package http.filter;

import http.HSession;
import http.exception.HttpErrorException;

public interface IHttpFilter {
	public boolean httpFilter(HSession hSession) throws HttpErrorException;
}
