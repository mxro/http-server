package de.mxro.httpserver;


public interface HttpsServerConfiguration extends HttpServerConfiguration {

    public abstract boolean getUseSsl();

    public abstract SslKeyStoreData getSslKeyStore();

}
