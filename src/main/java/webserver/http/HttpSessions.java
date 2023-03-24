package webserver.http;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpSessions {
	private static final Map<String, HttpSession> httpSessionMap = new ConcurrentHashMap<>();
	private HttpSessions() {
	}

	public static void save(HttpSession httpSession) {
		httpSessionMap.put(httpSession.getId(), httpSession);
	}

	public static HttpSession get(String id) {
		if (!httpSessionMap.containsKey(id)) {
			save(new HttpSession(id));
		}
		return httpSessionMap.get(id);
	}

	public static void remove(String id) {
		httpSessionMap.remove(id);
	}
}
