package webserver;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpContentType {
	TEXT_PLAIN("txt", "text/plain;charset=utf-8"),
	TEXT_HTML("html", "text/html;charset=utf-8"),
	TEXT_CSS("css", "text/css;charset=utf-8"),
	APPLICATION_JAVASCRIPT("js", "application/javascript;charset=utf-8"),
	IMAGE_PNG("png", "image/png"),
	IMAGE_JPEG("jpg", "image/jpeg"),
	IMAGE_GIF("gif", "image/gif"),
	APPLICATION_PDF("pdf", "application/pdf"),
	FONT_TTF("ttf", "font/ttf"),
	FONT_OTF("otf", "font/otf"),
	FONT_WOFF("woff", "font/woff"),
	FONT_WOFF2("woff2", "font/woff2"),
	IMAGE_X_ICON("ico", "image/x-icon"),
	APPLICATION_OCTET_STREAM("", "application/octet-stream");

	private static final Map<String, HttpContentType> EXTENSION_TO_CONTENT_TYPE;

	static {
		EXTENSION_TO_CONTENT_TYPE = Arrays.stream(values())
			.collect(Collectors.toMap(HttpContentType::getExtension, Function.identity()));

	}

	private final String extension;
	private final String contentType;

	HttpContentType(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public static String getContentType(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

		return Optional.ofNullable(
			EXTENSION_TO_CONTENT_TYPE.get(extension)
		).map(
			HttpContentType::getContentType
		).orElse(
			APPLICATION_OCTET_STREAM.getContentType()
		);
	}

	public String getContentType() {
		return contentType;
	}

	public String getExtension() {
		return extension;
	}
}


