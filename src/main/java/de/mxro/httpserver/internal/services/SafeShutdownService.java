package de.mxro.httpserver.internal.services;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.jre.ServiceJre;
import de.mxro.service.utils.ActivityMonitor;
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
	ActivityMonitor activityMonitor;
	
	@Override
	public void process(final Request request, final Response response,
			final Closure<SuccessFail> callback) {
		if (isShutdown.get()) {
			response.setResponseCode(503);
			response.setContent("Service "+this.getClass().getName()+" is in shutdown procedure.");
			response.setMimeType("text/plain");
			callback.apply(SuccessFail.success());
			return;
		}

		activityMonitor.notifyOperationStarted();
		
		decorated.process(request, response, new Closure<SuccessFail>() {

			@Override
			public void apply(SuccessFail o) {
				activityMonitor.notifyOperationCompleted();
				callback.apply(o);
			}
		});
	}

	@Override
	public void stop(final SimpleCallback callback) {
		isShutdown.set(true);
		
		if (activityMonitor.pendingOperations() == 0) {
			decorated.stop(callback);
			return;
		}
		
		new Thread() {

			@Override
			public void run() {
				shutdownAttempts.incrementAndGet();
				try {
					Thread.sleep(shutdownAttempts.get());
				} catch (InterruptedException e) {
					callback.onFailure(e);
					return;
				}
				
				if (shutdownAttempts.get() > 50) {
					callback.onFailure(new Exception("Failed to shutdown service: ["+decorated+"]"));
					return;
				}
				
				SafeShutdownService.this.stop(callback);
				
			}
			
			
			
		}.start();
		
	}

	@Override
	public void start(SimpleCallback callback) {
		assert this.activityMonitor.pendingOperations() == 0;
		
		this.isShutdown.set(false);
		this.shutdownAttempts.set(0);
		
		this.decorated.start(callback);
	}

	public SafeShutdownService(HttpService decorated) {
		super();
		this.decorated = decorated;
		this.isShutdown = new AtomicBoolean(false);
		this.activityMonitor = ServiceJre.createActivityMonitor();
		this.shutdownAttempts = new AtomicInteger(0);
	}

	
	
	
}
