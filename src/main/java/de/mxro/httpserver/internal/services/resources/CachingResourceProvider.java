package de.mxro.httpserver.internal.services.resources;

import java.util.HashMap;
import java.util.Map;

import de.mxro.httpserver.ResourceProvider;

public class CachingResourceProvider implements ResourceProvider {

    private final ResourceProvider decorated;

    private final Map<String, Resource> cache;

    private final static class Null implements Resource {

        @Override
        public byte[] getData() {
            throw new RuntimeException("null");
        }

        @Override
        public String getMimetype() {
            throw new RuntimeException("null");
        }
    };

    private final static Resource NULL = new Null();

    @Override
    public Resource getResource(final String path) {
        if (cache.containsKey(path)) {
            final Resource resource = cache.get(path);
            if (resource == NULL) {
                return null;
            }
            return resource;
        }

        final Resource resource = decorated.getResource(path);

        if (resource != null) {

            cache.put(path, resource);
        } else {
            cache.put(path, NULL);
        }

        return resource;
    }

    public CachingResourceProvider(final ResourceProvider decorated) {
        super();
        this.decorated = decorated;
        this.cache = new HashMap<String, Resource>(100);
    }

}