package webserver.handler;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public interface Handler {
	boolean isPossible(HttpRequest httpRequest);

	void handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
