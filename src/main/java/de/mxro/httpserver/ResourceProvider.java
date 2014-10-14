package de.mxro.httpserver;

public interface ResourceProvider {

	public interface Resource {
		public byte[] getData();

		public String getMimetype();
	}

	public Resource getResource(String path);

}
