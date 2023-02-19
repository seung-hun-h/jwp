package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {
	private final Map<String, String> headers = new HashMap<>();
	private String protocol = "HTTP/1.1";
	private final HttpStatus httpStatus;

	public HttpResponseHeader(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public HttpResponseHeader(String protocol, HttpStatus httpStatus) {
		this.protocol = protocol;
		this.httpStatus = httpStatus;
	}

	public void putHeader(String key, String value) {
		headers.put(key, value);
	}

	public void addCookie(String key, String value) {
		String cookies = headers.getOrDefault("Set-Cookie", "");

		if (cookies.isBlank()) {
			cookies = key + "=" + value;
		} else {
			cookies += "," + key + "=" + value;
		}

		headers.put("Set-Cookie", cookies);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(String.format("%s %d %s", protocol, httpStatus.getStatusCode(), httpStatus.getReason()))
			.append("\r\n");

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			stringBuilder.append(
				String.format("%s: %s",
					entry.getKey(),
					entry.getValue()
				)
			).append("\r\n");
		}

		return stringBuilder.toString();
	}
}
