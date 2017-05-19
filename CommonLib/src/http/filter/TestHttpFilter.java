package http.filter;

import http.HSession;

public class TestHttpFilter implements IHttpFilter {

	@Override
	public boolean httpFilter(HSession hSession) {

		return true;
	}

}
