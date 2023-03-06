package webserver.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

public class HttpResponse {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
	private static final String STATIC_RESOURCE_ROOT = System.getProperty("user.dir") + "/webapp";

	private final DataOutputStream dataOutputStream;

	private final HttpResponseHeader httpResponseHeader;

	public HttpResponse(OutputStream outputStream) {
		this.dataOutputStream = new DataOutputStream(outputStream);
		this.httpResponseHeader = new HttpResponseHeader(HttpStatus.OK);
	}

	public void sendRedirect(String url) {
		try {
			httpResponseHeader.setStatus(HttpStatus.FOUND);
			httpResponseHeader.putHeader(HttpHeaders.LOCATION, url);
			responseHeader();
			dataOutputStream.flush();
		} catch (IOException exception) {
			LOGGER.error(exception.getMessage());
		}
	}

	private String processHeader() {
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

	public void addCookie(Cookie cookie) {
		this.httpResponseHeader.addCookie(cookie);
	}

	public void forwardBody(String body) {
		forwardBody(body, MediaType.HTML_UTF_8);
	}

	public void forwardBody(String body, MediaType mediaType) {
		try {
			byte[] contents = body.getBytes();
			httpResponseHeader.setStatus(HttpStatus.OK);
			httpResponseHeader.putHeader(HttpHeaders.CONTENT_TYPE, mediaType.type());
			responseHeader(contents.length);
			responseBody(contents);
		} catch (IOException exception) {
			LOGGER.error(exception.getMessage());
		}
	}

	public void forward(String url) {
		try {
			byte[] contents = Files.readAllBytes(
				Path.of(STATIC_RESOURCE_ROOT + url));

			httpResponseHeader.setStatus(HttpStatus.OK);
			httpResponseHeader.putHeader(HttpHeaders.CONTENT_TYPE, HttpContentType.getContentType(url));
			responseHeader(contents.length);
			responseBody(contents);
		} catch (IOException exception) {
			LOGGER.error(exception.getMessage());
		}
	}

	private void responseHeader(int bodyLength) throws IOException {
		httpResponseHeader.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(bodyLength));
		responseHeader();
	}

	private void responseHeader() throws IOException {
		String header = processHeader();
		dataOutputStream.writeBytes(header);
		dataOutputStream.writeBytes("\r\n");
	}

	private void responseBody(byte[] body) throws IOException {
		dataOutputStream.write(body, 0, body.length);
		dataOutputStream.writeBytes("\r\n");
		dataOutputStream.flush();
	}
}
