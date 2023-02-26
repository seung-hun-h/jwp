package webserver.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.google.common.net.HttpHeaders;
import webserver.http.HttpContentType;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpResponseHeader;
import webserver.http.HttpStatus;

public class ResourceHandler implements Handler {
	private static final String ROOT = "/";
	private static final String STATIC_RESOURCE_ROOT = System.getProperty("user.dir") + "/webapp";
	private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile("(?:\\/[a-z0-9_\\-.]+)*\\/[a-z0-9_\\-.]+\\.[a-z]+$");

	@Override
	public boolean isPossible(HttpRequest httpRequest) {
		String requestUri = httpRequest.getRequestUri();
		return requestUri.equals(ROOT) ||
			STATIC_RESOURCE_PATTERN.matcher(
				requestUri
			).matches();
	}

	@Override
	public HttpResponse handle(HttpRequest httpRequest) {
		try {
			HttpResponseHeader header = new HttpResponseHeader(HttpStatus.OK);

			header.putHeader(
				HttpHeaders.CONTENT_TYPE,
				HttpContentType.getContentType(
					httpRequest.getRequestUri()
				)
			);

			byte[] body = serve(httpRequest.getRequestUri());
			header.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length));

			return new HttpResponse(header, body);
		} catch (IOException exception) {
			HttpResponseHeader header = new HttpResponseHeader(HttpStatus.INTERNAL_SERVER_ERROR);
			return new HttpResponse(header);
		}
	}

	private byte[] serve(String path) throws IOException {
		if (isRoot(path)) {
			return "Hello world!".getBytes();
		}

		return Files.readAllBytes(
			Path.of(STATIC_RESOURCE_ROOT + path));
	}

	private boolean isRoot(String path) {
		return path == null || path.isBlank() || path.equals(ROOT);
	}
}