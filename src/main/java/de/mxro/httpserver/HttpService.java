package de.mxro.httpserver;

import de.mxro.service.Service;
import delight.functional.Closure;
import delight.functional.SuccessFail;

public interface HttpService extends Service {

	public void process(Request request, Response response, Closure<SuccessFail> callback);
	
}
