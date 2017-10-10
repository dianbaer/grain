package org.grain.httpclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;

public class TrustStrategyAll implements TrustStrategy {

	@Override
	public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		return true;
	}

}
