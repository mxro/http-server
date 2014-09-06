package de.mxro.httpserver.internal.services;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.jre.ServiceJre;
import de.mxro.service.utils.OperationCounter;
import de.mxro.service.utils.ShutdownHelper;

/**
 * <P>
 * Assures that decorated service is only shut down if there are no active
 * requests.
 * 
 * @author Max
 * 
 */
public class SafeShutdownService implements HttpService {

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

        System.out.println("HERE " + decorated);
        this.shutdownHelper.shutdown(callback);
        // this.operationCounter = null;
        // this.shutdownHelper = null;
    }

    @Override
    public void start(final SimpleCallback callback) {
        this.operationCounter = ServiceJre.createOperationCounter();
        this.shutdownHelper = ServiceJre.createShutdownHelper(operationCounter);

        this.decorated.start(callback);
    }

    public SafeShutdownService(final HttpService decorated) {
        super();
        this.decorated = decorated;
        this.operationCounter = ServiceJre.createOperationCounter();
        this.shutdownHelper = ServiceJre.createShutdownHelper(operationCounter);
    }

}
