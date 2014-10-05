package de.mxro.httpserver;

import de.mxro.httpserver.internal.ResponseData;

public class HttpServer {

    public static Response createResponse() {
        return new ResponseData();
    }

    public static RequestData createRequest() {
        return new RequestData();
    }

    public static Request alterUri(final Request forRequest, final String newUri) {
        final RequestData altered = new RequestData(forRequest);
        altered.setRequestUri(newUri);
        return altered;
    }

}
