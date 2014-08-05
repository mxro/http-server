package de.mxro.httpserver.services;

import java.util.Map;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.internal.services.DispatchService;
import de.mxro.httpserver.internal.services.RequestTimeEnforcerService;
import de.mxro.httpserver.internal.services.SafeShutdownService;

public class Services {

	public final static HttpService dispatcher(Map<String, HttpService> serviceMap) {
		return Services.safeShutdown(new DispatchService(serviceMap));
	}

	public static HttpService safeShutdown(HttpService service) {
		return new SafeShutdownService(service);
	}
	
	
	public static HttpService limitTime(long maxCallTimeInMs, HttpService decoratedService) {
		return new RequestTimeEnforcerService(maxCallTimeInMs, decoratedService);
		
	}
	
	
}
