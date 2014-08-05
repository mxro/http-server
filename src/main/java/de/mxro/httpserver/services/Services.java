package de.mxro.httpserver.services;

import java.util.Map;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.StoppableHttpService;
import de.mxro.httpserver.internal.services.DispatchService;
import de.mxro.httpserver.internal.services.DummyShutdownService;
import de.mxro.httpserver.internal.services.RequestTimeEnforcerService;
import de.mxro.httpserver.internal.services.SafeShutdownService;

public class Services {

	public final static StoppableHttpService dispatcher(Map<String, StoppableHttpService> serviceMap) {
		return Services.safeShutdown(new DispatchService(serviceMap));
	}

	public static StoppableHttpService safeShutdown(HttpService service) {
		return new SafeShutdownService(service);
	}
	
	/**
	 * Doesn't really add any logic but assures that service conforms to StoppableService interface.
	 * @param service
	 * @return
	 */
	public static StoppableHttpService dummyShutdown(HttpService service) {
		return new DummyShutdownService(service);
	}
	
	public static StoppableHttpService limitTime(long maxCallTimeInMs, HttpService decoratedService) {
		return new RequestTimeEnforcerService(maxCallTimeInMs, decoratedService);
		
	}
	
	
}
