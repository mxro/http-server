package de.mxro.httpserver.internal.services;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import de.mxro.async.callbacks.SimpleCallback;
import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import de.mxro.server.ServerComponent;
import de.mxro.service.callbacks.ShutdownCallback;

public final class ShutdownService implements HttpService {

    private final String secret;
    private final ServerComponent serverToShutdown;

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

                if (!requestUri.replace("/", "").equals(secret)) {

                    try {
                        Netty3Server
                        .sendHttpResponse(
                                e,
                                "Access denied. You must supply the master secret for this server as part of the url, eg: http://myserver.com:8900/[your secret]"
                                .getBytes("UTF-8"), 403, "text/plain");

                        return;
                    } catch (final Throwable t) {
                        throw new RuntimeException(t);
                    }

                }

                shutdownOperations.stop(new ShutdownCallback() {

                    @Override
                    public void onSuccess() {

                        try {
                            // new Exception("Shutdown successful")
                            // .printStackTrace();
                            Netty3Server.sendHttpSuccess(e, "Shutdown successful.".getBytes("UTF-8"), "text/plain");

                            final TimerTask stopShutdownServer = new TimerTask() {

                                @Override
                                public void run() {
                                    assert thisServer != null : "setThisServer() must be specified for this shutdown server.";

                                    thisServer.stop(new ShutdownCallback() {

                                        @Override
                                        public void onSuccess() {
                                            // all ok

                                        }

                                        @Override
                                        public void onFailure(final Throwable t) {
                                            throw new RuntimeException(t);
                                        }
                                    });
                                }

                            };

                            new Timer().schedule(stopShutdownServer, 150);

                        } catch (final UnsupportedEncodingException e1) {
                            throw new RuntimeException(e1);
                        }

                    }

                    @Override
                    public void onFailure(final Throwable t) {

                        System.out.println("Failure while shutting down server: " + t.getMessage());
                        t.printStackTrace();

                        Netty3Server.sendHttpSuccess(e, t.getMessage().getBytes(), "text/plain");

                    }

                });
            }

        };

        t.start();
    }

    public ShutdownService(final String secret, final ServerComponent serverToShutdown) {
        super();
        this.secret = secret;
        this.serverToShutdown = serverToShutdown;
    }

}
