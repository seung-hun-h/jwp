package handler;

import webserver.HttpRequest;
import webserver.HttpResponse;

public interface Handler {
	boolean isPossible(HttpRequest httpRequest);

	HttpResponse handle(HttpRequest httpRequest);
}
