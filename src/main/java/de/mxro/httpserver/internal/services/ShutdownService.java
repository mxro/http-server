package de.mxro.httpserver.internal.services;

import java.util.Timer;
import java.util.TimerTask;

import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.server.ServerComponent;
import de.mxro.service.callbacks.ShutdownCallback;
import delight.async.Value;
import delight.async.callbacks.SimpleCallback;

public final class ShutdownService implements HttpService {

    private final String shutdownSecret;
    private final ServerComponent serverToShutdown;
    private final Value<ServerComponent> thisServer;

    @Override
    public void stop(final SimpleCallback callback) {
        callback.onSuccess();
    }

    @Override
    public void start(final SimpleCallback callback) {
        callback.onSuccess();
    }

    @Override
    public void process(final Request request, final Response response, final Closure<SuccessFail> callback) {
        final Thread t = new Thread() {

            @Override
            public void run() {

                final String requestUri = request.getRequestUri();
                final String suppliedSecret = requestUri.replace("/", "");

                if (!suppliedSecret.equals(shutdownSecret)) {

                    try {
                        response.setResponseCode(403);
                        response.setMimeType("text/plain");
                        response.setContent("Access denied. You must supply the master secret for this server as part of the url, eg: http://myserver.com:8900/[your secret]");
                        callback.apply(SuccessFail.success());

                        return;
                    } catch (final Throwable t) {
                        throw new RuntimeException(t);
                    }

                }

                serverToShutdown.stop(new ShutdownCallback() {

                    @Override
                    public void onSuccess() {

                        response.setResponseCode(200);
                        response.setMimeType("text/plain");
                        response.setContent("Shutdown successful.");
                        callback.apply(SuccessFail.success());

                        final TimerTask stopShutdownServer = new TimerTask() {

                            @Override
                            public void run() {
                                assert thisServer.get() != null : "thisServer must be specified for this shutdown server.";

                                thisServer.get().stop(new ShutdownCallback() {

                                    @Override
                                    public void onSuccess() {
                                        // System.out.println("This server stopped.");
                                        // all ok

                                    }

                                    @Override
                                    public void onFailure(final Throwable t) {
                                        throw new RuntimeException(t);
                                    }
                                });
                            }

                        };

                        new Timer().schedule(stopShutdownServer, 350);

                    }

                    @Override
                    public void onFailure(final Throwable t) {

                        System.out.println("Failure while shutting down server: " + t.getMessage());
                        t.printStackTrace();

                        callback.apply(SuccessFail.fail(t));

                    }

                });
            }

        };

        t.start();
    }

    public ShutdownService(final String secret, final ServerComponent serverToShutdown,
            final Value<ServerComponent> thisServer) {
        super();
        this.shutdownSecret = secret;
        this.serverToShutdown = serverToShutdown;
        this.thisServer = thisServer;
    }

}
