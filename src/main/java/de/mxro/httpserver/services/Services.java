package de.mxro.httpserver.services;

import java.util.Map;

import de.mxro.async.Value;
import de.mxro.fn.Function;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.internal.services.DispatchService;
import de.mxro.httpserver.internal.services.EchoService;
import de.mxro.httpserver.internal.services.FilterService;
import de.mxro.httpserver.internal.services.MetricsService;
import de.mxro.httpserver.internal.services.ProxyService;
import de.mxro.httpserver.internal.services.RequestTimeEnforcerService;
import de.mxro.httpserver.internal.services.ResourceService;
import de.mxro.httpserver.internal.services.SafeShutdownGuard;
import de.mxro.httpserver.internal.services.ShutdownService;
import de.mxro.httpserver.internal.services.StaticDataService;
import de.mxro.httpserver.internal.services.TrackRequestTimeService;
import de.mxro.httpserver.resources.ResourceProvider;
import de.mxro.metrics.MetricsNode;
import de.mxro.server.ServerComponent;

public final class Services {

    public final static HttpService dispatcher(final Map<String, HttpService> serviceMap) {
        return Services.safeShutdown(new DispatchService(serviceMap));
    }

    public static HttpService safeShutdown(final HttpService service) {
        return new SafeShutdownGuard(service);
    }

    public static HttpService limitTime(final long maxCallTimeInMs, final HttpService decoratedService) {
        return new RequestTimeEnforcerService(maxCallTimeInMs, decoratedService);

    }

    /**
     * <p>
     * Create a service, which renders the provided metrics node.
     * 
     * @param metrics
     * @return
     */
    public static HttpService metrics(final MetricsNode metrics) {
        return new MetricsService(metrics);
    }

    public static HttpService trackRequestTimes(final MetricsNode metrics, final String metricsId,
            final HttpService decorated) {
        return new TrackRequestTimeService(metricId, metrics, decorated);
    }

    public final static HttpService filter(final Function<Request, Boolean> test, final HttpService primary,
            final HttpService secondary) {
        return new FilterService(test, primary, secondary);
    }

    /**
     * A service which returns what was sent to it.
     * 
     * @return
     */
    public final static HttpService echo() {
        return new EchoService();
    }

    /**
     * Allows to serve the same byte array for every request. Useful for
     * robots.txt etc.
     * 
     * @param data
     * @param contentType
     * @return
     */
    public final static HttpService data(final byte[] data, final String contentType) {
        return new StaticDataService(data, contentType);
    }

    /**
     * Allows to server static files from a directory or the classpath.
     * 
     * @param provider
     * @return
     */
    public static HttpService resources(final ResourceProvider provider) {
        return new ResourceService(provider);
    }

    public static HttpService forward(final String destinationHost, final int destinationPort) {

        return new ProxyService(destinationHost, destinationPort);
    }

    public static HttpService shutdown(final String secret, final ServerComponent serverToShutdown,
            final Value<ServerComponent> ownServer) {
        return new ShutdownService(secret, serverToShutdown, ownServer);
    }

}
