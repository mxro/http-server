package de.mxro.httpserver.internal.services;

import io.nextweb.fn.Closure;
import io.nextweb.fn.SuccessFail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.service.callbacks.ShutdownCallback;

public class DispatchService implements HttpService {

	private final Map<String, HttpService> serviceMap;
	
	@Override
	public void process(Request request, Response response,
			Closure<SuccessFail> callback) {
		
		final String uri = request.getRequestUri();
		for (Entry<String, HttpService> e : serviceMap.entrySet()) {
			if ( uri.startsWith(e.getKey())) {
				e.getValue().process(request, response, callback);
				return;
			}
		}
		
		for (Entry<String, HttpService> e : serviceMap.entrySet()) {
			if (e.getKey().equals("*")) {
				e.getValue().process(request, response, callback);
			}
		}
		
	}

	@Override
	public void stop(ShutdownCallback callback) {
		ArrayList<HttpService> services = new ArrayList<HttpService>();
		
		for (Entry<String, HttpService> e : serviceMap.entrySet()) {
			services.add(e.getValue());
		}
		
		stop(services, 0, callback);
	}

	private void stop(final List<HttpService> services, final int serviceIdx, final ShutdownCallback callback) {
		if (serviceIdx >= services.size()) {
			callback.onShutdownComplete();
			return;
		}
		
		services.get(serviceIdx).stop(new Closure<SuccessFail>() {

			@Override
			public void apply(SuccessFail o) {
				if (o.isFail()) {
					callback.apply(o);
					return;
				}
				
				stop(services, serviceIdx+1, callback);
			}
		});
	}
	
	public DispatchService(Map<String, StoppableHttpService> serviceMap) {
		super();
		this.serviceMap = serviceMap;
	}

	
	
}
