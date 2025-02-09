package webserver.http;

public enum HttpStatus {
	OK(200, "OK"),
	FOUND(302, "Found"),
	UNAUTHORIZED(401, "Unauthorized"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error");

	private final int statusCode;
	private final String reason;

	HttpStatus(int statusCode, String reason) {
		this.statusCode = statusCode;
		this.reason = reason;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getReason() {
		return reason;
	}
}
