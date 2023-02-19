package webserver;

public class HttpResponse {
	private final HttpResponseHeader httpResponseHeader;
	private final byte[] httpBody;

	public HttpResponse(HttpResponseHeader httpResponseHeader) {
		this(httpResponseHeader, new byte[0]);
	}

	public HttpResponse(HttpResponseHeader httpResponseHeader, byte[] httpBody) {
		this.httpResponseHeader = httpResponseHeader;
		this.httpBody = httpBody;
	}

	public String getHttpResponseHeader() {
		return httpResponseHeader.toString();
	}

	public byte[] getHttpBody() {
		return httpBody;
	}

	@Override
	public String toString() {
		return "HttpResponse{" +
			"httpResponseHeader=" + httpResponseHeader +
			", httpBody=" + new String(httpBody) +
			'}';
	}
}
