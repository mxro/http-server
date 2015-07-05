package de.mxro.httpserver.internal.services;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.jre.ServicesJre;
import de.mxro.service.utils.OperationCounter;
import de.mxro.service.utils.ShutdownHelper;
import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

/**
 * <P>
 * Assures that decorated service is only shut down if there are no active
 * requests.
 * 
 * @author Max
 * 
 */
public class SafeShutdownGuard implements HttpService {

    private final HttpService decorated;

    ShutdownHelper shutdownHelper;
    OperationCounter operationCounter;

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {
        if (this.shutdownHelper.isShuttingDown()) {
            response.setResponseCode(503);
            response.setContent("Service " + this.getClass().getName() + " is in shutdown procedure.");
            response.setMimeType("text/plain");
            callback.apply(SuccessFail.success());
            return;
        }

        operationCounter.increase();

        decorated.process(request, response, new Closure<SuccessFail>() {

            @Override
            public void apply(final SuccessFail o) {
                operationCounter.decrease();
                callback.apply(o);
            }
        });
    }

    @Override
    public void stop(final SimpleCallback callback) {

        // new Exception("HERE " + decorated).printStackTrace();
        this.shutdownHelper.shutdown(new SimpleCallback() {

            @Override
            public void onFailure(final Throwable t) {
                callback.onSuccess();
            }

            @Override
            public void onSuccess() {

                decorated.stop(callback);
            }
        });
        // this.operationCounter = null;
        // this.shutdownHelper = null;
    }

    @Override
    public void start(final SimpleCallback callback) {
        this.operationCounter = ServicesJre.createOperationCounter();
        this.shutdownHelper = ServicesJre.createShutdownHelper(operationCounter);

        this.decorated.start(callback);
    }

    public SafeShutdownGuard(final HttpService decorated) {
        super();
        this.decorated = decorated;
        this.operationCounter = ServicesJre.createOperationCounter();
        this.shutdownHelper = ServicesJre.createShutdownHelper(operationCounter);
    }

}
