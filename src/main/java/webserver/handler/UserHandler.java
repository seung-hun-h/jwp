package webserver.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.http.HttpMethod;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpResponseHeader;
import webserver.http.HttpStatus;

public class UserHandler implements Handler {
	private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

	@Override
	public boolean isPossible(HttpRequest httpRequest) {
		return httpRequest.getRequestUri()
			.startsWith("/user");
	}

	@Override
	public HttpResponse handle(HttpRequest httpRequest) {
		if (isCreateUserRequest(httpRequest)) {
			return createUser(
				HttpRequestUtils.parseQueryString(
					httpRequest.getHttpBody()
				)
			);
		}

		if (isLoginRequest(httpRequest)) {
			return login(
				HttpRequestUtils.parseQueryString(
					httpRequest.getHttpBody()
				)
			);
		}

		throw new IllegalStateException(
			String.format("Cannot handle request. http request = %s", httpRequest)
		);
	}

	private boolean isCreateUserRequest(HttpRequest httpRequest) {
		return httpRequest.getHttpMethod() == HttpMethod.POST &&
			httpRequest.getRequestUri()
				.equals("/user/create");
	}

	private boolean isLoginRequest(HttpRequest httpRequest) {
		return httpRequest.getHttpMethod() == HttpMethod.POST &&
			httpRequest.getRequestUri()
				.equals("/user/login");
	}

	private HttpResponse createUser(Map<String, String> params) {
		User user = new User(
			params.get("userId"),
			params.get("password"),
			params.get("name"),
			params.get("email")
		);

		DataBase.addUser(user);

		log.info("User is created. user = {}", user);

		HttpResponseHeader header = new HttpResponseHeader(HttpStatus.FOUND);
		header.putHeader("Location", "/index.html");

		return new HttpResponse(header);
	}


	private HttpResponse login(Map<String, String> params) {
		User user = DataBase.findUserById(
			params.get("userId")
		);

		boolean logined = false;
		if (user != null) {
			logined = user.login(params.get("password"));
		}

		HttpResponseHeader header = new HttpResponseHeader(HttpStatus.FOUND);
		header.putHeader(HttpHeaders.CONTENT_LENGTH, "0");
		header.addCookie("logined", String.valueOf(logined));

		if (logined) {
			header.putHeader("Location", "/index.html");
		} else {
			header.putHeader("Location", "/user/login_failed.html");
		}

		return new HttpResponse(header);
	}
}
