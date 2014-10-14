package de.mxro.httpserver.services;

import java.util.Map;

import de.mxro.fn.Function;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.internal.services.DispatchService;
import de.mxro.httpserver.internal.services.EchoService;
import de.mxro.httpserver.internal.services.FilterService;
import de.mxro.httpserver.internal.services.RequestTimeEnforcerService;
import de.mxro.httpserver.internal.services.ResourceService;
import de.mxro.httpserver.internal.services.SafeShutdownService;
import de.mxro.httpserver.internal.services.StaticDataService;
import de.mxro.httpserver.resources.ResourceProvider;

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

    /**
     * Allows to server static files from a directory or the classpath.
     * 
     * @param provider
     * @return
     */
    public static HttpService newStaticResourceHandler(final ResourceProvider provider) {
        return new ResourceService(provider);
    }

    /**
     * Allows to serve the same byte array for every request. Useful for
     * robots.txt etc.
     * 
     * @param data
     * @param contentType
     * @return
     */
    public static HttpService newStaticDataHandler(final byte[] data, final String contentType) {
        return new StaticDataService(data, contentType);
    }

}
