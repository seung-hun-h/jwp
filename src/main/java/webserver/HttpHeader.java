package webserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.net.HttpHeaders;
import util.HttpRequestUtils;

public class HttpHeader {
	private final HttpMethod httpMethod;
	private final HttpRequestUri httpRequestUri;
	private final String protocol;
	private final Map<String, String> fields = new HashMap<>();

	private HttpHeader(HttpMethod httpMethod, HttpRequestUri httpRequestUri, String protocol) {
		this.httpMethod = httpMethod;
		this.httpRequestUri = httpRequestUri;
		this.protocol = protocol;
	}

	public static HttpHeader from(List<String> headers) {
		if (headers == null || headers.isEmpty()) {
			throw new IllegalArgumentException(String.format("invalid header. headers: %s", headers));
		}

		Map<String, String> firstHeaderLine = HttpRequestUtils.getFirstHeaderLine(headers.get(0));

		HttpMethod httpMethod = HttpMethod.from(firstHeaderLine.get("method"));
		HttpRequestUri httpRequestUri = HttpRequestUri.from(firstHeaderLine.get("uri"));
		String protocol = firstHeaderLine.get("protocol");

		HttpHeader header = new HttpHeader(httpMethod, httpRequestUri, protocol);

		for (int i = 1; i < headers.size(); i++) {
			HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(headers.get(i));

			if (pair != null) {
				header.putField(pair.getKey(), pair.getValue());
			}
		}

		return header;
	}

	private void putField(String key, String value) {
		this.fields.put(key, value);
	}

	public String getHeader(String key) {
		return this.fields.get(key);
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public HttpRequestUri getHttpRequestUri() {
		return httpRequestUri;
	}

	public String getProtocol() {
		return protocol;
	}

	public int getContentLength() {
		String value = fields.getOrDefault(HttpHeaders.CONTENT_LENGTH, "0");
		return Integer.parseInt(value);
	}

	@Override
	public String toString() {
		return "HttpHeader{" +
			"httpMethod=" + httpMethod +
			", httpRequestUri=" + httpRequestUri +
			", protocol='" + protocol + '\'' +
			", fields=" + fields +
			'}';
	}
}
