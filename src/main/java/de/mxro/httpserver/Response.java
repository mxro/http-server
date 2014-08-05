package de.mxro.httpserver;

import java.util.Map;

public interface Response {

	public void setContent(byte[] data);
	
	/**
	 * Set string as content. String will be encoded in UTF-8.
	 * @param content
	 */
	public void setContent(String content);
	
	
	public void setResponseCode(int responseCode);
	
	public void setMimeType(String mimeType);
	
	public String getMimeType();
	
	public void setHeader(String key, String value);
	
	public Map<String, String> getHeaders();
	
	public int getResponseCode();
	
	public byte[] getContent();
	
	/**
	 * Copy all values from the given response.
	 * 
	 * @param from
	 */
	public void setAll(Response from);
	
}
