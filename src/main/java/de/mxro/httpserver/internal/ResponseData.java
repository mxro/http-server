package de.mxro.httpserver.internal;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mxro.httpserver.Response;

public class ResponseData implements Response {

	private byte[] content;
	private int responseCode;
	String mimeType;
	Map<String, String> headers;
	
	@Override
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	
	
	@Override
	public void setContent(String content) {
		try {
			this.setContent(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public String getMimeType() {
		
		return mimeType;
	}

	@Override
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
		setHeader("Content-Type", mimeType);
	}

	@Override
	public void setHeader(String key, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(key, value);
	}

	@Override
	public Map<String, String> getHeaders() {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		return headers;
	}
	
	
	
	@Override
	public void setAll(Response from) {
		this.setContent(from.getContent());
		this.setMimeType(from.getMimeType());
		this.setResponseCode(from.getResponseCode());
		
		this.headers.clear();
		
		for (Entry<String, String> e: from.getHeaders().entrySet()) {
			this.setHeader(e.getKey(), e.getValue());
		}
		
		
	}

	public ResponseData() {
		super();
		this.content = new byte[] {};
		this.responseCode = 200;
		this.mimeType = "text/plain";
		this.headers = null;
	}

}
