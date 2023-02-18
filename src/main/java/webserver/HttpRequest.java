package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.IOUtils;

public class HttpRequest {
	private final HttpHeader httpHeader;
	private final String httpBody;

	private HttpRequest(HttpHeader httpHeader, String httpBody) {
		this.httpHeader = httpHeader;
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

		HttpHeader httpHeader = HttpHeader.from(httpHeaders);

		String body = IOUtils.readData(
			bufferedReader,
			httpHeader.getContentLength()
		);

		return new HttpRequest(httpHeader, body);
	}

	public String getRequestUri() {
		return this.httpHeader
			.getHttpRequestUri()
			.getUri();
	}

	public String getHttpBody() {
		return httpBody;
	}

	public HttpMethod getHttpMethod() {
		return httpHeader.getHttpMethod();
	}

	public HttpHeader getHttpHeader() {
		return httpHeader;
	}

	@Override
	public String toString() {
		return "HttpRequest{" +
			"httpHeader=" + httpHeader +
			", httpBody='" + httpBody + '\'' +
			'}';
	}
}
