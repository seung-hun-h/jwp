package Handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import webserver.HttpRequest;

public class ResourceHandler implements Handler {
	private static final String ROOT = "/";
	private static final String STATIC_RESOURCE_ROOT = System.getProperty("user.dir") + "/webapp";
	private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile("(\\/[a-zA-Z0-9]{1,})+(\\.[a-zA-Z0-9]{1,5})$");

	@Override
	public boolean isPossible(HttpRequest httpRequest) {
		String requestUri = httpRequest.getRequestUri();
		return requestUri.equals(ROOT) ||
			STATIC_RESOURCE_PATTERN.matcher(
				requestUri
			).matches();
	}

	public byte[] serve(String path) throws IOException {
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