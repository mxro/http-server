package de.mxro.httpserver.internal.services;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public class FilterService implements HttpService {

    private final HttpService primary;
    private final HttpService secondary;

    @Override
    public void stop(final SimpleCallback callback) {
        primary.start(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                secondary.stop(callback);
            }
        });

    }

    @Override
    public void start(final SimpleCallback callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {
        // TODO Auto-generated method stub

    }

}
