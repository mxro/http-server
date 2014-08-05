package de.mxro.httpserver;

import java.io.InputStream;
import java.util.Map;

/**
 * Generally NOT thread-safe.
 * 
 * @author Max
 *
 */
public interface Request {

	public InputStream getInputStream();
	
	public byte[] getData();
	public Map<String, String> getHeaders();
	
	public String getRequestUri();
	
	public String getHeader(String key);
	
	public HttpMethod getMethod();
	
	/**
	 * @return The IP address of the partner from which this requests originates.
	 */
	public Address getSourceAddress();
	
}
