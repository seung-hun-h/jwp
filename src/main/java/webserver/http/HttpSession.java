package webserver.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {
	private final String id;
	private final Map<String, Object> attributes;

	public HttpSession(String id) {
		this.id = id;
		this.attributes = new HashMap<>();
	}

	public String getId() {
		return id;
	}

	public void setAttribute(String name, Object attribute) {
		attributes.put(name, attribute);
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	public void invalidate() {
		HttpSessions.remove(id);
	}
}
