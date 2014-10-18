package de.mxro.httpserver.resources;

public interface Resource {

    public byte[] getData();

    public String getMimetype();

    private long getLastModified();

}