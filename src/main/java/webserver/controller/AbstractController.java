package webserver.controller;

import webserver.http.HttpMethod;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public abstract class AbstractController implements Controller {
	@Override
	public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
		HttpMethod httpMethod = httpRequest.getHttpMethod();

		if (httpMethod.isPost()) {
			doPost(httpRequest, httpResponse);
		}

		doGet(httpRequest, httpResponse);
	}

	abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

	abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
