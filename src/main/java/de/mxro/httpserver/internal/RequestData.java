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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHeader(final String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HttpMethod getMethod() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Address getSourceAddress() {
        // TODO Auto-generated method stub
        return null;
    }

}
