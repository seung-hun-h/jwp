package webserver.http;

import java.util.HashMap;
import java.util.Map;

import util.HttpRequestUtils;

public class HttpRequestUri {
	private static final String QUERY_STRING_SEPARATOR = "?";
	private final String uri;
	private final Map<String, String> queryParam = new HashMap<>();

	public HttpRequestUri(String uri) {
		this.uri = uri;
	}

	public static HttpRequestUri from(String httpUri) {
		if (isNotContainsQueryStringSeparator(httpUri)) {
			return new HttpRequestUri(httpUri);
		}

		HttpRequestUri httpRequestUri = new HttpRequestUri(httpUri.substring(0, httpUri.indexOf(QUERY_STRING_SEPARATOR)));

		String queryString = httpUri.substring(httpUri.indexOf(QUERY_STRING_SEPARATOR) + 1);
		Map<String, String> parseQueryString = HttpRequestUtils.parseQueryString(queryString);

		httpRequestUri.putQueryParams(parseQueryString);

		return httpRequestUri;
	}

	private static boolean isNotContainsQueryStringSeparator(String httpUri) {
		return !httpUri.contains(QUERY_STRING_SEPARATOR);
	}

	public void putQueryParams(Map<String, String> queryParam) {
		this.queryParam.putAll(queryParam);
	}

	public String getUri() {
		return this.uri;
	}

	public String getQueryValue(String key) {
		return queryParam.get(key);
	}

	@Override
	public String toString() {
		return "HttpRequestUri{" +
			"uri='" + uri + '\'' +
			", queryParam=" + queryParam +
			'}';
	}
}
