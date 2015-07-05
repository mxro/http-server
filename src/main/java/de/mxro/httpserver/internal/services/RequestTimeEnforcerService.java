package de.mxro.httpserver.internal.services;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.callbacks.ShutdownCallback;
import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

public class RequestTimeEnforcerService implements HttpService {

    private final HttpService decorated;
    private final RequestTimeEnforcementThread thread;

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {

        final RequestTimeEntry entry = thread.logRequest(request, response, callback);

        decorated.process(request, response, new Closure<SuccessFail>() {

            @Override
            public void apply(final SuccessFail o) {
                if (thread.logSuccess(entry)) {
                    callback.apply(o);
                    return;
                }

                // callback already called
                throw new IllegalStateException(
                        "Response for service could not be sent since service was timed out OR callback was called twice.\n"
                                + "  Decorated Service: " + decorated + "\n" + "  Request: " + request + "\n"
                                + "  Response: " + response + "\n" + "  Callback: " + callback);
            }
        });

    }

    @Override
    public void stop(final SimpleCallback callback) {

        decorated.stop(new ShutdownCallback() {

            @Override
            public void onSuccess() {

                thread.shutdown();
                callback.onSuccess();

            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });

    }

    public RequestTimeEnforcerService(final long maxTime, final HttpService decorated) {
        super();

        this.decorated = decorated;
        this.thread = new RequestTimeEnforcementThread(maxTime);
    }

    @Override
    public void start(final SimpleCallback callback) {
        this.thread.setName(this.getClass() + "-watching-" + decorated.getClass());
        this.thread.setPriority(Thread.MIN_PRIORITY);
        this.thread.start();
        this.decorated.start(callback);
    }

}
