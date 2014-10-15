package de.mxro.httpserver;

import de.mxro.sslutils.SslKeyStoreData;

public interface HttpsServerConfiguration extends HttpServerConfiguration {

    public abstract boolean getUseSsl();

    public abstract SslKeyStoreData getSslKeyStore();

}
