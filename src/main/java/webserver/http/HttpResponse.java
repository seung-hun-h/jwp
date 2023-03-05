package webserver.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.google.common.net.HttpHeaders;

public class HttpResponse {
	private HttpResponseHeader httpResponseHeader;
	private byte[] httpBody;
	private String errorMessage;

	private final DataOutputStream dataOutputStream;

	public HttpResponse(OutputStream outputStream) {
		this.dataOutputStream = new DataOutputStream(outputStream);
		this.httpBody = new byte[0];
		this.httpResponseHeader = new HttpResponseHeader(HttpStatus.OK);
	}

	public void setHttpResponseHeader(HttpResponseHeader httpResponseHeader) {
		this.httpResponseHeader = httpResponseHeader;
	}

	public void sendRedirect(String uri) {
		httpResponseHeader.setStatus(HttpStatus.FOUND);
		httpResponseHeader.putHeader(HttpHeaders.LOCATION, uri);
	}

	public void writeBody(String body) {
		this.httpBody = body.getBytes();
		httpResponseHeader.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(httpBody.length));
	}

	public void writeBody(byte[] body) {
		this.httpBody = body;
		httpResponseHeader.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(httpBody.length));
	}

	public void flush() {
		try {
			String header = writeHeaders();
			this.dataOutputStream.writeBytes(header);
			this.dataOutputStream.writeBytes("\r\n");
			this.dataOutputStream.write(this.httpBody);
			this.dataOutputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String writeHeaders() {
		StringBuilder stringBuilder = new StringBuilder();

		String protocol = httpResponseHeader.getProtocol();
		HttpStatus httpStatus = httpResponseHeader.getHttpStatus();
		stringBuilder.append(String.format(
					"%s %d %s",
					protocol,
					httpStatus.getStatusCode(),
					httpStatus.getReason()
				)
			)
			.append("\r\n");

		Map<String, String> headers = httpResponseHeader.getHeaders();
		for (Map.Entry<String, String> header : headers.entrySet()) {
			stringBuilder.append(
				String.format("%s: %s",
					header.getKey(),
					header.getValue()
				)
			).append("\r\n");
		}

		List<Cookie> cookies = httpResponseHeader.getCookies();

		for (Cookie cookie : cookies) {
			stringBuilder.append(String.format("%s: %s=%s", HttpHeaders.SET_COOKIE, cookie.getName(), cookie.getValue()));

			if (cookie.getPath() != null) {
				stringBuilder.append("; Path=")
					.append(cookie.getPath());
			}

			stringBuilder.append("\r\n");
		}

		return stringBuilder.toString();
	}

	public void sendError(HttpStatus httpStatus, String errorMessage) {
		this.httpResponseHeader = new HttpResponseHeader(httpStatus);
		this.errorMessage = errorMessage;
	}

	public void addCookie(Cookie cookie) {
		this.httpResponseHeader.addCookie(cookie);
	}
}
