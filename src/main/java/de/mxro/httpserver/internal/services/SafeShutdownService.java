package de.mxro.httpserver.internal.services;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.nextweb.fn.Closure;
import io.nextweb.fn.SuccessFail;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.StoppableHttpService;

public class SafeShutdownService implements StoppableHttpService {

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
	public void stop(final Closure<SuccessFail> callback) {
		isShutdown.set(true);
		
		if (activeRequests.get() == 0) {
			if (!(decorated instanceof StoppableHttpService)) {
				callback.apply(SuccessFail.success());
				return;
			}
			
			((StoppableHttpService) decorated).stop(callback);
			return;
		}
		
		new Thread() {

			@Override
			public void run() {
				shutdownAttempts.incrementAndGet();
				try {
					Thread.sleep(shutdownAttempts.get());
				} catch (InterruptedException e) {
					callback.apply(SuccessFail.fail(e));
					return;
				}
				
				if (shutdownAttempts.get() > 50) {
					callback.apply(SuccessFail.fail(new Exception("Failed to shutdown service: ["+decorated+"]")));
					return;
				}
				
				SafeShutdownService.this.stop(callback);
				
			}
			
			
			
		}.start();
		
	}

	public SafeShutdownService(HttpService decorated) {
		super();
		this.decorated = decorated;
		this.isShutdown = new AtomicBoolean(false);
		this.activeRequests = new AtomicInteger(0);
		this.shutdownAttempts = new AtomicInteger(0);
	}

	
	
	
}
