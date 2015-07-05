package de.mxro.httpserver.internal.services;

import delight.async.callbacks.SimpleCallback;
import delight.functional.Closure;
import delight.functional.SuccessFail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mxro.concurrency.jre.ConcurrencyJre;
import de.mxro.concurrency.wrappers.SimpleExecutor;
import de.mxro.concurrency.wrappers.SimpleExecutor.WhenExecutorShutDown;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public final class ProxyService implements HttpService {

    private final String destinationHost;
    private final int destinationPort;

    private SimpleExecutor executor;

    // <!-- one.download
    // https://u1.linnk.it/qc8sbw/usr/apps/textsync/files/fragements-copy-stream
    // -->
    /**
     * Copy Streams from Apache Commons
     * 
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[2024];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    // <!-- one.end -->

    @Override
    public final void process(final Request request, final Response response, final Closure<SuccessFail> callback) {
        this.executor.execute(new Runnable() {

            @Override
            public void run() {
                final byte[] bytes = request.getData();

                try {

                    final HttpURLConnection connection = (HttpURLConnection) new URL("http://" + destinationHost + ":"
                            + destinationPort + request.getRequestUri()).openConnection();

                    connection.setConnectTimeout(320000);
                    connection.setReadTimeout(320000);

                    for (final Entry<String, String> headerEntry : request.getHeaders().entrySet()) {
                        connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                    }

                    if (bytes.length > 0) {
                        connection.setDoInput(true);
                    }
                    connection.setDoOutput(true);

                    connection.connect();

                    if (bytes.length > 0) {
                        final OutputStream os = connection.getOutputStream();

                        os.write(bytes, 0, bytes.length);

                        os.flush();

                        os.close();
                    }

                    final InputStream is = connection.getInputStream();

                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    copy(is, bos);

                    is.close();
                    final byte[] responseBytes = bos.toByteArray();

                    final Map<String, List<String>> headerFields = connection.getHeaderFields();

                    response.setContent(responseBytes);
                    response.setResponseCode(connection.getResponseCode());

                    for (final Entry<String, List<String>> e : headerFields.entrySet()) {
                        response.setHeader(e.getKey(), e.getValue().get(0));
                    }

                    callback.apply(SuccessFail.success());

                } catch (final Throwable e1) {
                    callback.apply(SuccessFail.fail(e1));
                    return;
                }
            }
        });
    }

    public ProxyService(final String destinationHost, final int destinationPort) {
        super();

        this.destinationHost = destinationHost;
        this.destinationPort = destinationPort;

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

}
