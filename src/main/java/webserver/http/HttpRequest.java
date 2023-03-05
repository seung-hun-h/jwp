package webserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.IOUtils;

public class HttpRequest {
	private final HttpRequestHeader httpRequestHeader;
	private final String httpBody;

	private HttpRequest(HttpRequestHeader httpRequestHeader, String httpBody) {
		this.httpRequestHeader = httpRequestHeader;
		this.httpBody = httpBody;
	}

	public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
		String line = bufferedReader.readLine();
		if (line == null) {
			throw new IllegalArgumentException("Wrong Http Request");
		}

		List<String> httpHeaders = new ArrayList<>();
		while (!line.isBlank()) {
			httpHeaders.add(line);
			line = bufferedReader.readLine();
		}

		HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(httpHeaders);

		String body = IOUtils.readData(
			bufferedReader,
			httpRequestHeader.getContentLength()
		);

		return new HttpRequest(httpRequestHeader, body);
	}

	public String getRequestUri() {
		return this.httpRequestHeader
			.getHttpRequestUri()
			.getUri();
	}

	public String getHttpBody() {
		return httpBody;
	}

	public HttpMethod getHttpMethod() {
		return httpRequestHeader.getHttpMethod();
	}

	public Cookie getCookie(String cookieName) {
		return httpRequestHeader.getCookies()
			.stream()
			.filter(cookie -> cookie.getName().equals(cookieName))
			.findAny()
			.orElse(null);
	}

	@Override
	public String toString() {
		return "HttpRequest{" +
			"httpHeader=" + httpRequestHeader +
			", httpBody='" + httpBody + '\'' +
			'}';
	}
}
