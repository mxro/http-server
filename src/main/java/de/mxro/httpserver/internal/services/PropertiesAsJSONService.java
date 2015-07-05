package de.mxro.httpserver.internal.services;

import delight.async.callbacks.SimpleCallback;
import delight.async.callbacks.ValueCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

import de.mxro.async.properties.PropertyNode;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public class PropertiesAsJSONService implements HttpService {

    private final PropertyNode properties;

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
        properties.render(new ValueCallback<String>() {

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

    public PropertiesAsJSONService(final PropertyNode metrics) {
        super();
        this.properties = metrics;
    }

}
