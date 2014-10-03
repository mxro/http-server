package de.mxro.httpserver.internal;

import java.io.InputStream;
import java.util.Map;

import de.mxro.httpserver.Address;
import de.mxro.httpserver.HttpMethod;
import de.mxro.httpserver.Request;

public final class RequestData implements Request {

    private InputStream inputStream;
    private byte[] data;
    private Map<String, String> headers;
    private String requestUri;
    private HttpMethod method;

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public String getHeader(final String key) {
        return headers.get(key);
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public Address getSourceAddress() {
        return address;

    }

}
