package de.mxro.httpserver.internal.services;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.callbacks.ShutdownCallback;
import de.mxro.service.callbacks.StartCallback;

/**
 * Assures that decorated service is only shut down if there are no active requests.
 * @author Max
 *  
 */
public class SafeShutdownService implements HttpService {

	private final HttpService decorated;
	AtomicBoolean isShutdown;
	AtomicInteger activeRequests;
	AtomicInteger shutdownAttempts;
	
	@Override
	public void process(final Request request, final Response response,
			final Closure<SuccessFail> callback) {
		activeRequests.incrementAndGet();
		if (isShutdown.get()) {
			activeRequests.decrementAndGet();
			response.setResponseCode(503);
			response.setContent("Service "+this.getClass().getName()+" is in shutdown procedure.");
			response.setMimeType("text/plain");
			callback.apply(SuccessFail.success());
			return;
		}

		decorated.process(request, response, new Closure<SuccessFail>() {

			@Override
			public void apply(SuccessFail o) {
				activeRequests.decrementAndGet();
				callback.apply(o);
			}
		});
	}

	@Override
	public void stop(final ShutdownCallback callback) {
		isShutdown.set(true);
		
		if (activeRequests.get() == 0) {
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
	public void start(StartCallback callback) {
		assert this.activeRequests.get() == 0;
		
		this.isShutdown.set(false);
		this.activeRequests.set(0);
		this.shutdownAttempts.set(0);
		
		this.decorated.start(callback);
	}

	public SafeShutdownService(HttpService decorated) {
		super();
		this.decorated = decorated;
		this.isShutdown = new AtomicBoolean(false);
		this.activeRequests = new AtomicInteger(0);
		this.shutdownAttempts = new AtomicInteger(0);
	}

	
	
	
}
