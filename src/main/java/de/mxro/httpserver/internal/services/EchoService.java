package de.mxro.httpserver.internal.services;

import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public class EchoService implements HttpService {

    @Override
    public void stop(final SimpleCallback callback) {
        callback.onSuccess();
    }

    @Override
    public void start(final SimpleCallback callback) {
        callback.onSuccess();
    }

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {

        response.setResponseCode(200);
        response.setContent(request.getData());
        response.setHeader("REQUEST-URI", request.getRequestUri());
        callback.apply(SuccessFail.success());
    }

}
