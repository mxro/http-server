package de.mxro.httpserver;

import io.nextweb.fn.Closure;
import io.nextweb.fn.SuccessFail;
import de.mxro.service.Service;

public interface HttpService extends Service {

	public void process(Request request, Response response, Closure<SuccessFail> callback);
	
}
