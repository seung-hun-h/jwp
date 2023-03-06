package webserver.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.net.HttpHeaders;

public class HttpResponseHeader {
	private final Map<String, String> headers = new HashMap<>();
	private final List<Cookie> cookies = new ArrayList<>();

	private String protocol = "HTTP/1.1";
	private HttpStatus httpStatus;

	public HttpResponseHeader(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public void putHeader(String key, String value) {
		putHeaderInternal(key, value);
	}

	private void putHeaderInternal(String key, String value) {
		if (key.equals(HttpHeaders.SET_COOKIE)) {
			String[] splitValue = value.split("=");
			addCookie(new Cookie(splitValue[0], splitValue[1]));
			return;
		}

		this.headers.put(key, value);
	}

	public void addCookie(Cookie newCookie) {
		this.cookies.add(newCookie);
	}

	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	public List<Cookie> getCookies() {
		return Collections.unmodifiableList(cookies);
	}

	public String getProtocol() {
		return protocol;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
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

	public void setStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
