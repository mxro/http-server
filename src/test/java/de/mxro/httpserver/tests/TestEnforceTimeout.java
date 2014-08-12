package de.mxro.httpserver.tests;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpServer;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.httpserver.internal.services.RequestTimeEnforcerService;

public class TestEnforceTimeout {

	
	@Test
	public void testNoTimeout() throws InterruptedException {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final RequestTimeEnforcerService testService = new RequestTimeEnforcerService(100, new HttpService() {
			
			@Override
			public void process(Request request, Response response,
					Closure<SuccessFail> callback) {
				
				callback.apply(SuccessFail.success());
			}

			@Override
			public void stop(SimpleCallback callback) {
				callback.onSuccess();
			}

			@Override
			public void start(SimpleCallback callback) {
				callback.onSuccess();
			}
		});
		
		testService.start(new SimpleCallback() {
			
			@Override
			public void onSuccess() {
				testService.process(HttpServer.createDummyRequest(), HttpServer.createResponse(), new Closure<SuccessFail>() {

					@Override
					public void apply(SuccessFail o) {
						latch.countDown();
					}
				});
			}
			
			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		});
		
		
		
		latch.await();
		
		
	}
	
}
