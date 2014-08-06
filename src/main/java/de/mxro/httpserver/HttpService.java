package de.mxro.httpserver;

import de.mxro.fn.Closure;
import de.mxro.fn.SuccessFail;
import de.mxro.service.Service;

public interface HttpService extends Service {

	public void process(Request request, Response response, Closure<SuccessFail> callback);
	
}
