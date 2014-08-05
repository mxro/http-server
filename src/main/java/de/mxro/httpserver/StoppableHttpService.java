package de.mxro.httpserver;

import io.nextweb.fn.Closure;
import io.nextweb.fn.SuccessFail;

public interface StoppableHttpService extends HttpService {
	
	public void stop(Closure<SuccessFail> callback);
	
}
