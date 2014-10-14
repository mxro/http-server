package de.mxro.httpserver.internal.services.resources;

import de.mxro.httpserver.ResourceProvider;

public class WebResourceProvider implements ResourceProvider {

    private final ResourceProvider decorated;

    @Override
    public Resource getResource(final String path) {
        final Resource resource = decorated.getResource(path);

        if (resource == null) {
            return null;
        }

        String mimetype = resource.getMimetype();
        if (path.endsWith(".html")) {
            mimetype = "text/html";
        } else if (path.endsWith(".js")) {
            mimetype = "text/javascript"; // correct would be
                                          // application/javascript but
                                          // text/javascript more compatible
        } else if (path.endsWith(".png")) {
            mimetype = "image/png";
        } else if (path.endsWith(".gif")) {
            mimetype = "image/gif";
        } else if (path.endsWith(".css")) {
            mimetype = "text/css";
        } else if (path.endsWith(".rpc")) {
            mimetype = "text/plain";
        }

        final String closedMimetype = mimetype;
        return new Resource() {

            @Override
            public byte[] getData() {
                return resource.getData();
            }

            @Override
            public String getMimetype() {
                return closedMimetype;
            }

        };
    }

    public WebResourceProvider(final ResourceProvider decorated) {
        super();
        this.decorated = decorated;
    }

}
