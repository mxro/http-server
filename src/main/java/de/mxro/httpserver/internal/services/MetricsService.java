package de.mxro.httpserver.internal.services;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.async.callbacks.ValueCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.metrics.MetricsNode;

public class MetricsService implements HttpService {

    private final MetricsNode metrics;

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
        metrics.render(new ValueCallback<String>() {

            @Override
            public void onFailure(final Throwable t) {
                callback.apply(SuccessFail.fail(t));
            }

            @Override
            public void onSuccess(final String value) {
                response.setContent(value);
                response.setMimeType("application/json");
                response.setResponseCode(200);

                callback.apply(SuccessFail.success());
            }
        });

    }

    public MetricsService(final MetricsNode metrics) {
        super();
        this.metrics = metrics;
    }

}
