package de.mxro.httpserver;

import de.mxro.httpserver.internal.DummyRequestData;
import de.mxro.httpserver.internal.ResponseData;

public class HttpServer {

    public static Response createResponse() {
        return new ResponseData();
    }

    public static Request createDummyRequest() {
        return new DummyRequestData();
    }

    public static Request alterUri(final String newUri, final Request forRequest) {

    }

}
