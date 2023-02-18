package Handler;

import webserver.HttpRequest;

public interface Handler {
	boolean isPossible(HttpRequest httpRequest);
}
