package de.mxro.httpserver.resources;

public interface Resource {

    public byte[] data();

    public String mimetype();

    public long lastModified();

}