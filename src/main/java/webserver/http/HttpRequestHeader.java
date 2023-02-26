package webserver.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.net.HttpHeaders;
import util.HttpRequestUtils;

public class HttpRequestHeader {
	private static final String COOKIE_SEPARATOR = ",";
	private static final String EMPTH_STRING = "";

	private final HttpMethod httpMethod;
	private final HttpRequestUri httpRequestUri;
	private final String protocol;
	private final Map<String, String> fields = new HashMap<>();

	private HttpRequestHeader(HttpMethod httpMethod, HttpRequestUri httpRequestUri, String protocol) {
		this.httpMethod = httpMethod;
		this.httpRequestUri = httpRequestUri;
		this.protocol = protocol;
	}

	public static HttpRequestHeader from(List<String> headers) {
		if (headers == null || headers.isEmpty()) {
			throw new IllegalArgumentException(String.format("invalid header. headers: %s", headers));
		}

		Map<String, String> firstHeaderLine = HttpRequestUtils.getFirstHeaderLine(headers.get(0));

		HttpMethod httpMethod = HttpMethod.from(firstHeaderLine.get("method"));
		HttpRequestUri httpRequestUri = HttpRequestUri.from(firstHeaderLine.get("uri"));
		String protocol = firstHeaderLine.get("protocol");

		HttpRequestHeader header = new HttpRequestHeader(httpMethod, httpRequestUri, protocol);

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

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public HttpRequestUri getHttpRequestUri() {
		return httpRequestUri;
	}

	public int getContentLength() {
		String value = fields.getOrDefault(HttpHeaders.CONTENT_LENGTH, "0");
		return Integer.parseInt(value);
	}

	public String getCookie(String cookieName) {
		Map<String, String> cookie = HttpRequestUtils.parseCookies(fields.getOrDefault("Cookie", EMPTH_STRING));

		if (notExistCookie(cookie, cookieName)) {
			return "";
		}

		return cookie.get(cookieName)
			.split(COOKIE_SEPARATOR)[0];
	}

	private boolean notExistCookie(Map<String, String> cookie, String cookieName) {
		return !cookie.containsKey(cookieName);
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
