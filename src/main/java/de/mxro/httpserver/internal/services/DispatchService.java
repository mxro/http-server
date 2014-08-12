package de.mxro.httpserver.internal.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
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
			if (uri.startsWith(e.getKey())) {
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

	private static void stop(final List<HttpService> services,
			final int serviceIdx, final ShutdownCallback callback) {
		if (serviceIdx >= services.size()) {
			callback.onSuccess();
			return;
		}

		services.get(serviceIdx).stop(new ShutdownCallback() {

			@Override
			public void onSuccess() {
				stop(services, serviceIdx + 1, callback);
			}

			@Override
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}
		});
	}

	public DispatchService(Map<String, HttpService> serviceMap) {
		super();
		this.serviceMap = serviceMap;
	}

	@Override
	public void start(SimpleCallback callback) {
		
		ArrayList<HttpService> services = new ArrayList<HttpService>();

		for (Entry<String, HttpService> e : serviceMap.entrySet()) {
			services.add(e.getValue());
		}

		start(services, 0, callback);
	}

	private static void start(final List<HttpService> services,
			final int serviceIdx, final SimpleCallback callback) {
		if (serviceIdx >= services.size()) {
			callback.onSuccess();
			return;
		}
		
		services.get(serviceIdx).start(new SimpleCallback() {
			
			@Override
			public void onSuccess() {
				start(services, serviceIdx+1, callback);
			}
			
			@Override
			public void onFailure(Throwable t) {
				callback.onFailure(t);
			}
		});
	}

}
