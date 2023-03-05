package webserver.http;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

public class Cookie {
	private static final Map<String, String> ATTRIBUTES = new HashMap<>();
	private static final String PATH = "Path";

	private final String name;
	private final String value;

	public Cookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public void addPath(String uri) {
		addAttributes(PATH, uri);
	}

	public String getPath() {
		return getAttribute(PATH);
	}

	private void addAttributes(String key, String value) {
		ATTRIBUTES.put(key, value);
	}

	private String getAttribute(String key) {
		return ATTRIBUTES.get(key);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Cookie cookie = (Cookie)o;
		return Objects.equal(name, cookie.name) && Objects.equal(value, cookie.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name, value);
	}
}
