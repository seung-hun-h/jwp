package webserver.handler;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public interface Handler {
	boolean isPossible(HttpRequest httpRequest);

	HttpResponse handle(HttpRequest httpRequest);
}
