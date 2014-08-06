package de.mxro.httpserver.internal.services;

import io.nextweb.fn.SuccessFail;
import de.mxro.fn.Closure;
import de.mxro.httpserver.Request;
import de.mxro.httpserver.Response;

public class RequestTimeEntry {

	private final Request request;
	private final Response response;
	private final Closure<SuccessFail> callback;
	private final long started;
	
	public Request getRequest() {
		return request;
	}

	public Response getResponse() {
		return response;
	}

	public Closure<SuccessFail> getCallback() {
		return callback;
	}

	public long getStarted() {
		return started;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestTimeEntry other = (RequestTimeEntry) obj;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		return true;
	}

	public RequestTimeEntry(Request request, Response response,
			Closure<SuccessFail> callback, long started) {
		super();
		this.request = request;
		this.response = response;
		this.callback = callback;
		this.started = started;
	}

}
