package de.mxro.httpserver.internal.services;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.server.ServerComponent;

public final class ShutdownService implements HttpService {

    private final String secret;
    private final ServerComponent serverToShutdown;

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

    }

    public ShutdownService(final String secret, final ServerComponent serverToShutdown) {
        super();
        this.secret = secret;
        this.serverToShutdown = serverToShutdown;
    }

}
