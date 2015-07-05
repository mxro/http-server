package de.mxro.httpserver.internal.services;

import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public final class DispatchService implements HttpService {

    private final Map<String, HttpService> serviceMap;

    @Override
    public final void process(final Request request, final Response response, final Closure<SuccessFail> callback) {
        // TODO use more efficient data structure
        final String uri = request.getRequestUri();
        for (final Entry<String, HttpService> e : serviceMap.entrySet()) {
            if (uri.startsWith(e.getKey())) {
                e.getValue().process(request, response, callback);
                return;
            }
        }

        for (final Entry<String, HttpService> e : serviceMap.entrySet()) {
            if (e.getKey().equals("*")) {
                e.getValue().process(request, response, callback);
                return;
            }
        }

    }

    @Override
    public void stop(final SimpleCallback callback) {

        // System.out.println("stop ....");

        final ArrayList<HttpService> services = new ArrayList<HttpService>();

        for (final Entry<String, HttpService> e : serviceMap.entrySet()) {
            services.add(e.getValue());
        }

        stop(services, 0, callback);
    }

    private static void stop(final List<HttpService> services, final int serviceIdx, final SimpleCallback callback) {
        if (serviceIdx >= services.size()) {
            callback.onSuccess();
            return;
        }

        services.get(serviceIdx).stop(new SimpleCallback() {

            @Override
            public void onSuccess() {
                stop(services, serviceIdx + 1, callback);
            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public DispatchService(final Map<String, HttpService> serviceMap) {
        super();
        this.serviceMap = serviceMap;
    }

    @Override
    public void start(final SimpleCallback callback) {

        final ArrayList<HttpService> services = new ArrayList<HttpService>();

        for (final Entry<String, HttpService> e : serviceMap.entrySet()) {
            services.add(e.getValue());
        }

        start(services, 0, callback);
    }

    private static void start(final List<HttpService> services, final int serviceIdx, final SimpleCallback callback) {
        if (serviceIdx >= services.size()) {
            callback.onSuccess();
            return;
        }

        services.get(serviceIdx).start(new SimpleCallback() {

            @Override
            public void onSuccess() {
                start(services, serviceIdx + 1, callback);
            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
