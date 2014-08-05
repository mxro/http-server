package de.mxro.httpserver;

import io.nextweb.fn.Closure;
import io.nextweb.fn.SuccessFail;

public interface HttpService {

	public void process(Request request, Response response, Closure<SuccessFail> callback);
	
}
