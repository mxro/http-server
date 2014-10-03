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
    private Address address;

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

    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public void setHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void setRequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public void setMethod(final HttpMethod method) {
        this.method = method;
    }

    public void setAddress(final Address address) {
        this.address = address;
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
