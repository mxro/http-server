package de.mxro.httpserver.resources;

import java.io.File;

import de.mxro.httpserver.internal.services.resources.CachingResourceProvider;
import de.mxro.httpserver.internal.services.resources.ClassResourceProvider;
import de.mxro.httpserver.internal.services.resources.FileResourceProvider;
import de.mxro.httpserver.internal.services.resources.WebResourceProvider;

public final class Resources {

    /**
     * Annotates common file types of the web with the proper mimetype based on
     * their file extension.
     * 
     * @param decorated
     * @return
     */
    public final static ResourceProvider forWeb(final ResourceProvider decorated) {
        return new WebResourceProvider(decorated);
    }

    /**
     * Caches ALL accessed resources in memory using a WeakHashMap.
     * 
     * @param decorated
     * @return
     */
    public static ResourceProvider cache(final ResourceProvider decorated) {
        return new CachingResourceProvider(decorated);
    }

    /**
     * Loads files from the local file system.
     * 
     * @param resourceRoot
     * @param serverRoot
     * @param resourcesRoot
     * @return
     */
    public final static ResourceProvider fromFiles(final File resourceRoot, final String serverRoot) {
        return new FileResourceProvider(resourceRoot, serverRoot);
    }

    /**
     * Uses getClass().getResourceAsString() on the provided resourceRoot object
     * to select resources in Java packages.
     * 
     * @param resourceRoot
     * @param serverRoot
     * @param resourcesRoot
     * @return
     */
    public static ResourceProvider fromClasspath(final Class<?> resourceRoot, final String serverRoot,
            final String resourcesRoot) {
        return new ClassResourceProvider(resourceRoot, serverRoot, resourcesRoot);
    }

}
