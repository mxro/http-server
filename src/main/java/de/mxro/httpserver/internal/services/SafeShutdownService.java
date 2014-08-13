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
 * <P>Assures that decorated service is only shut down if there are no active requests.
 * 
 * @author Max
 *  
 */
public class SafeShutdownService implements HttpService {

	private final HttpService decorated;
	
	ShutdownHelper shutdownHelper;
	OperationCounter activityMonitor;
	
	@Override
	public void process(final Request request, final Response response,
			final Closure<SuccessFail> callback) {
		if (this.shutdownHelper.isShuttingDown()) {
			response.setResponseCode(503);
			response.setContent("Service "+this.getClass().getName()+" is in shutdown procedure.");
			response.setMimeType("text/plain");
			callback.apply(SuccessFail.success());
			return;
		}

		activityMonitor.increase();
		
		decorated.process(request, response, new Closure<SuccessFail>() {

			@Override
			public void apply(SuccessFail o) {
				activityMonitor.decrease();
				callback.apply(o);
			}
		});
	}

	@Override
	public void stop(final SimpleCallback callback) {
		this.shutdownHelper.shutdown(callback);
		this.activityMonitor = null;
		this.shutdownHelper = null;
	}

	@Override
	public void start(SimpleCallback callback) {
		this.activityMonitor = ServiceJre.createOperationCounter();
		this.shutdownHelper = ServiceJre.createShutdownHelper(activityMonitor);

		this.decorated.start(callback);
	}

	public SafeShutdownService(HttpService decorated) {
		super();
		this.decorated = decorated;
		this.activityMonitor = null;
		this.shutdownHelper = null;
	}

	
	
	
}
