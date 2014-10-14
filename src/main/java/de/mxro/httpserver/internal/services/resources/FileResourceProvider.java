package de.mxro.httpserver.internal.services.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.mxro.httpserver.resources.Resource;
import de.mxro.httpserver.resources.ResourceProvider;

public final class FileResourceProvider implements ResourceProvider {

    private final File resourceRoot;

    private final String serverRoot;

    @Override
    public final Resource getResource(final String path) {

        InputStream resourceAsStream;
        try {

            final String derivedPath = path.replaceFirst(serverRoot, resourceRoot.getAbsolutePath());

            resourceAsStream = new FileInputStream(new File(derivedPath));
        } catch (final FileNotFoundException e) {
            return null;
        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);

        copyLarge(resourceAsStream, bos);

        final byte[] data = bos.toByteArray();

        return new Resource() {

            @Override
            public String getMimetype() {
                return "";
            }

            @Override
            public byte[] getData() {
                return data;
            }
        };
    }

    public FileResourceProvider(final File resourceRoot, final String serverRoot) {
        super();
        this.resourceRoot = resourceRoot;
        this.serverRoot = serverRoot;
    }

    /**
     * FROM Apache Commons IOUtils!
     * 
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * 
     * @param input
     *            the <code>InputStream</code> to read from
     * @param output
     *            the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException
     *             if the input or output is null
     * @throws IOException
     *             if an I/O error occurs
     * @since Commons IO 1.3
     */
    private final static long copyLarge(final InputStream input, final OutputStream output) {
        try {
            final byte[] buffer = new byte[1024 * 4];
            long count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
