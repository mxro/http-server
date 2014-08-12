package de.mxro.httpserver.internal.services;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.callbacks.ShutdownCallback;

public class RequestTimeEnforcerService implements HttpService {

	private final HttpService decorated;
	private final RequestTimeEnforcementThread thread;

	@Override
	public void process(final Request request, final Response response,
			final Closure<SuccessFail> callback) {

		final RequestTimeEntry entry = thread.logRequest(request, response,
				callback);

		decorated.process(request, response, new Closure<SuccessFail>() {

			@Override
			public void apply(SuccessFail o) {
				if (thread.logSuccess(entry)) {
					callback.apply(o);
					return;
				}

				// callback already called
				throw new IllegalStateException(
						"Response for service could not be sent since service was timed out. "
								+ decorated);
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
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}
		});

	}

	public RequestTimeEnforcerService(long maxTime, HttpService decorated) {
		super();

		this.decorated = decorated;
		this.thread = new RequestTimeEnforcementThread(maxTime);
	}

	@Override
	public void start(SimpleCallback callback) {
		this.thread.setName(this.getClass()+"-watching-" + decorated.getClass());
		this.thread.setPriority(Thread.MIN_PRIORITY);
		this.thread.start();
		this.decorated.start(callback);
	}

	
	
	
}
