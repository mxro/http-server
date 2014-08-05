package de.mxro.httpserver.internal.services;

import io.nextweb.fn.Closure;
import io.nextweb.fn.SuccessFail;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.StoppableHttpService;

public class DummyShutdownService implements StoppableHttpService {

	private final HttpService decorated;
	
	@Override
	public void process(Request request, Response response,
			Closure<SuccessFail> callback) {
		decorated.process(request, response, callback);
	}

	@Override
	public void stop(Closure<SuccessFail> callback) {
		callback.apply(SuccessFail.success());
	}

	public DummyShutdownService(HttpService decorated) {
		super();
		if (decorated instanceof StoppableHttpService) {
			throw new IllegalArgumentException("StoppableService should not be supplied to dummy shutdown service. "+decorated);
		}
		
		this.decorated = decorated;
	}

	
	
}
