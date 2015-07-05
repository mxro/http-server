package de.mxro.httpserver.internal.services;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;
import delight.functional.Closure;
import delight.functional.SuccessFail;

public final class RequestTimeEnforcementThread extends Thread {

    private final long maxTime;
    private final AtomicBoolean isShutdown;
    private final AtomicBoolean shutdownComplete;
    private final ConcurrentLinkedQueue<RequestTimeEntry> requests;

    public RequestTimeEntry logRequest(final Request request, final Response response,
            final Closure<SuccessFail> callback) {
        final long started = System.currentTimeMillis();

        final RequestTimeEntry entry = new RequestTimeEntry(request, response, callback, started);

        requests.add(entry);
        return entry;
    }

    public boolean logSuccess(final RequestTimeEntry entry) {
        final boolean removed = requests.remove(entry);
        return removed;
    }

    public void shutdown() {
        isShutdown.set(true);

        while (!shutdownComplete.get()) {

            try {
                Thread.sleep(5);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public void run() {
        super.run();

        while (!isShutdown.get()) {
            final long now = System.currentTimeMillis();
            for (final RequestTimeEntry e : requests) {

                if (now - e.getStarted() > maxTime) {
                    requests.remove(e);

                    e.getResponse().setResponseCode(524);
                    e.getResponse().setMimeType("text/plain");
                    e.getResponse().setContent(
                            "The call could not be completed since it took longer than the maximum allowed time.");
                    e.getCallback().apply(SuccessFail.success());
                }
            }

            try {
                Thread.sleep(10);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        shutdownComplete.set(true);

    }

    public RequestTimeEnforcementThread(final long maxTime) {
        this.maxTime = maxTime;
        this.isShutdown = new AtomicBoolean(false);
        this.shutdownComplete = new AtomicBoolean(false);
        this.requests = new ConcurrentLinkedQueue<RequestTimeEntry>();
    }

}