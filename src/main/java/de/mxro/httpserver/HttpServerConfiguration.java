package de.mxro.httpserver;

import delight.factories.Configuration;

public interface HttpServerConfiguration extends Configuration {

    public abstract int port();

    public abstract HttpService service();

}
