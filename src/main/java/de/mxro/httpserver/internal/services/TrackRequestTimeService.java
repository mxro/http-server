package de.mxro.httpserver.internal.services;

import de.mxro.async.properties.PropertyNode;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.metrics.jre.Metrics;
import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

public class TrackRequestTimeService implements HttpService {

    private final String metricId;
    private final PropertyNode metrics;
    private final HttpService decorated;

    @Override
    public void stop(final SimpleCallback callback) {
        decorated.stop(callback);

    }

    @Override
    public void start(final SimpleCallback callback) {
        decorated.start(callback);
    }

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {

        final long start = System.currentTimeMillis();

        decorated.process(request, response, new Closure<SuccessFail>() {

            @Override
            public void apply(final SuccessFail o) {

                final long duration = System.currentTimeMillis() - start;

                metrics.record(Metrics.value(metricId, duration));

                callback.apply(o);
            }

        });

    }

    public TrackRequestTimeService(final String metricId, final PropertyNode metrics, final HttpService decorated) {
        super();
        this.metricId = metricId;
        this.metrics = metrics;
        this.decorated = decorated;
    }

}
