package de.mxro.httpserver.internal.services;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public final class StaticDataService implements HttpService {

    private final byte[] data;
    private final String contentType;

    public StaticDataService(final byte[] data, final String contentType) {
        super();
        this.data = data;
        this.contentType = contentType;
    }

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {
        response.setContent(data);
        response.setMimeType(contentType);
        callback.apply(SuccessFail.success());
    }

    @Override
    public void stop(final SimpleCallback callback) {
        callback.onSuccess();
    }

    @Override
    public void start(final SimpleCallback callback) {
        callback.onSuccess();
    }

}
