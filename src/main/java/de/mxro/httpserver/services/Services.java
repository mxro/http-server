package de.mxro.httpserver.services;

import java.util.Map;

import de.mxro.fn.Function;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.internal.services.DispatchService;
import de.mxro.httpserver.internal.services.EchoService;
import de.mxro.httpserver.internal.services.FilterService;
import de.mxro.httpserver.internal.services.RequestTimeEnforcerService;
import de.mxro.httpserver.internal.services.SafeShutdownService;
import de.mxro.httpserver.internal.services.StaticDataService;

public final class Services {

    public final static HttpService dispatcher(final Map<String, HttpService> serviceMap) {
        return Services.safeShutdown(new DispatchService(serviceMap));
    }

    public static HttpService safeShutdown(final HttpService service) {
        return new SafeShutdownService(service);
    }

    public static HttpService limitTime(final long maxCallTimeInMs, final HttpService decoratedService) {
        return new RequestTimeEnforcerService(maxCallTimeInMs, decoratedService);

    }

    public final static HttpService filter(final Function<Request, Boolean> test, final HttpService primary,
            final HttpService secondary) {
        return new FilterService(test, primary, secondary);
    }

    public final static HttpService dummyService() {
        return new EchoService();
    }

    public final static HttpService data(final byte[] data, final String contentType) {
        return new StaticDataService(data, contentType);
    }

}
