package de.mxro.httpserver.internal;

import java.io.InputStream;
import java.util.Map;

import de.mxro.httpserver.Address;
import de.mxro.httpserver.HttpMethod;
import de.mxro.httpserver.Request;

public class DummyRequestData implements Request{

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public byte[] getData() {
		return null;
	}

	@Override
	public Map<String, String> getHeaders() {
		return null;
	}

	@Override
	public String getRequestUri() {
		return null;
	}

	@Override
	public String getHeader(String key) {
		return null;
	}

	@Override
	public HttpMethod getMethod() {
		return null;
	}

	@Override
	public Address getSourceAddress() {
		return null;
	}

	public DummyRequestData() {
		super();
	}

	
	
}
