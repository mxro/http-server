package de.mxro.httpserver;

import delight.functional.Closure;
import delight.functional.SuccessFail;

import de.mxro.service.Service;

public interface HttpService extends Service {

	public void process(Request request, Response response, Closure<SuccessFail> callback);
	
}
