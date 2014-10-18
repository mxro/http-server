package de.mxro.httpserver.internal.services;

import java.util.Date;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.concurrency.jre.ConcurrencyJre;
import de.mxro.concurrency.wrappers.SimpleExecutor;
import de.mxro.concurrency.wrappers.SimpleExecutor.WhenExecutorShutDown;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.httpserver.resources.Resource;
import de.mxro.httpserver.resources.ResourceProvider;

public class ResourceService implements HttpService {

    private final ResourceProvider provider;

    private SimpleExecutor executor;

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {

        this.executor.execute(new Runnable() {

            @Override
            public void run() {

                final String requestUri = request.getRequestUri();

                final Resource resource = provider.getResource(requestUri);

                if (resource != null) {
                    response.setContent(resource.data());
                    response.setMimeType(resource.mimetype());

                    // Cache Validation
                    final String ifModifiedSinceHeader = request.getHeader("If-Modified-Since");
                    if (ifModifiedSinceHeader != null && !ifModifiedSinceHeader.equals("")) {

                        final Date ifModifiedSince = ResourceService_DateUtil
                                .parseDateFromHttpHeader(ifModifiedSinceHeader);

                        if (ifModifiedSince.getTime() < resource.lastModified()) {

                            response.setResponseCode(304); // not modified
                            callback.apply(SuccessFail.success());
                            return;
                        }

                    }

                    if (requestUri.contains(".nocache.")) {
                        // cache resources for at least 1 s
                        writeHeadersForCaching(response, 1000);

                        callback.apply(SuccessFail.success());
                        return;
                    } else {

                        final int maxCache = 60 * 60000;
                        writeHeadersForCaching(response, maxCache);

                        callback.apply(SuccessFail.success());
                        return;
                    }
                } else {
                    response.setResponseCode(404);
                    response.setContent(("Could not find resource: [" + requestUri + "]").getBytes());

                    callback.apply(SuccessFail.success());
                    return;
                }
            }

            private void writeHeadersForCaching(final Response response, final int maxCache) {
                final long now = System.currentTimeMillis();

                response.setHeader("Expires", (now + maxCache) + "");
                response.setHeader("Last-Modified", now + "");

                // cache control 'public' is important for resources to
                // be cached when SSL is used
                response.setHeader("Cache-Control", "max-age=" + maxCache + ", public");
            }
        });
    }

    @Override
    public void stop(final SimpleCallback callback) {
        this.executor.shutdown(new WhenExecutorShutDown() {

            @Override
            public void thenDo() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void start(final SimpleCallback callback) {

        this.executor = ConcurrencyJre.create().newExecutor().newParallelExecutor(5, this);

        callback.onSuccess();
    }

    public ResourceService(final ResourceProvider provider) {
        super();
        this.provider = provider;

    }

}
