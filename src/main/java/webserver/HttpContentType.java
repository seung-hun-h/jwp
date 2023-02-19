package webserver;

import java.util.Arrays;

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

	private final String extension;
	private final String contentType;

	HttpContentType(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public static String getContentType(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex == -1) {
			return APPLICATION_OCTET_STREAM.contentType;
		}

		String extension = fileName.substring(dotIndex + 1);
		return Arrays.stream(values())
			.filter(httpContentType -> httpContentType.extension.equals(extension))
			.findFirst()
			.map(httpContentType -> httpContentType.contentType)
			.orElse(APPLICATION_OCTET_STREAM.contentType);
	}

}


