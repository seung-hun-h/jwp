package webserver.http;

import java.util.Arrays;

public enum HttpMethod {
	GET, POST, PUT, DELETE;

	public static HttpMethod from(String method) {
		return Arrays.stream(values())
			.filter(httpMethod ->
				httpMethod.name()
					.equalsIgnoreCase(method))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(String.format("unknown http method. method: %s", method)));
	}

	public boolean isPost() {
		return this == POST;
	}
}
