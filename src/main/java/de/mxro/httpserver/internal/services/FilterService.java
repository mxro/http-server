package de.mxro.httpserver.internal.services;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

public final class FilterService implements HttpService {

    private final Function<Request, Boolean> test;
    private final HttpService primary;
    private final HttpService secondary;

    @Override
    public void stop(final SimpleCallback callback) {
        // System.out.println("stop " + secondary);
        primary.stop(new SimpleCallback() {

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
        secondary.start(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }

            @Override
            public void onSuccess() {
                primary.start(callback);
            }
        });
    }

    @Override
    public final void process(final Request request, final Response response, final Closure<SuccessFail> callback) {

        if (test.apply(request)) {
            this.primary.process(request, response, callback);
            return;
        }

        this.secondary.process(request, response, callback);

    }

    public FilterService(final Function<Request, Boolean> test, final HttpService primary, final HttpService secondary) {
        super();
        this.test = test;
        this.primary = primary;
        this.secondary = secondary;
    }

}
