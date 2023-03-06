package webserver.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import util.HttpRequestUtils;

public class HttpRequestHeader {
	private final HttpMethod httpMethod;
	private final HttpRequestUri httpRequestUri;
	private final String protocol;
	private final Map<String, String> fields = new HashMap<>();
	private final List<Cookie> cookies = new ArrayList<>();

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

			if (pair == null) {
				continue;
			}

			if (pair.getKey().equals(HttpHeaders.COOKIE)) {
				header.addCookie(pair.getValue());
			}
			header.putField(pair.getKey(), pair.getValue());
		}

		return header;
	}

	private void addCookie(String lawCookie) {
		Map<String, String> cookieMap = HttpRequestUtils.parseCookies(lawCookie);

		cookieMap.forEach((key, value) -> {
			Cookie cookie = new Cookie(key, value);
			cookies.add(cookie);
		});
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

	public List<Cookie> getCookies() {
		return new ArrayList<>(this.cookies);
	}

	public String getHeader(String key) {
		return fields.get(key);
	}

	public void addRequestParameters(Map<String, String> requestParameters) {
		httpRequestUri.putQueryParams(requestParameters);
	}

	public boolean isFormData() {
		return fields.getOrDefault(HttpHeaders.CONTENT_TYPE, "")
			.equals(MediaType.FORM_DATA.toString());
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

	public String getRequestParameter(String key) {
		return httpRequestUri.getQueryValue(key);
	}
}
