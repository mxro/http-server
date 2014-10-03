package de.mxro.httpserver.internal;

import java.io.InputStream;
import java.util.Map;

import de.mxro.httpserver.Address;
import de.mxro.httpserver.HttpMethod;
import de.mxro.httpserver.Request;

public final class RequestData implements Request {

    private final InputStream inputStream;
    private final byte[] data;
    private final Map<String, String> headers;
    private final String requestUri;
    private final HttpMethod method;
    private final Address address;

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

    public RequestData(final Request request) {
        super();
        this.inputStream = request.getInputStream();
        this.data = request.getData();
        this.headers = request.getHeaders();
        this.requestUri = request.getRequestUri();
        this.method = request.getMethod();
        this.address = request.getSourceAddress();
    }

}
