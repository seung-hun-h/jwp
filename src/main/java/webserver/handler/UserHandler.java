package webserver.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.http.HttpContentType;
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
			createUser(
				HttpRequestUtils.parseQueryString(
					httpRequest.getHttpBody()
				)
			);

			HttpResponseHeader header = new HttpResponseHeader(HttpStatus.FOUND);
			header.putHeader("Location", "/index.html");

			return new HttpResponse(header);
		}

		if (isLoginRequest(httpRequest)) {
			boolean logined = login(
				HttpRequestUtils.parseQueryString(
					httpRequest.getHttpBody()
				)
			);

			if (!logined) {
				return responseNotLogined();
			}

			HttpResponseHeader header = new HttpResponseHeader(HttpStatus.FOUND);
			header.putHeader(HttpHeaders.CONTENT_LENGTH, "0");
			header.addCookie("logined", "true");
			header.addCookiePath("/");
			header.putHeader("Location", "/index.html");
			return new HttpResponse(header);
		}

		if (isGetUserListRequest(httpRequest)) {
			String logined = httpRequest.getCookie("logined");

			if (isNotLogined(logined)) {
				return responseNotLogined();
			}

			List<User> users = getUsers();

			StringBuilder stringBuilder = new StringBuilder("<table>"
				+ "	<tr>"
				+ "		<th>id</th>"
				+ "		<th>name</th>"
				+ "		<th>email</th>"
				+ "	</tr>");

			stringBuilder.append("	<tbody>");
			for (User user : users) {
				stringBuilder.append("		<tr>");

				stringBuilder.append(String.format("			"
					+ "<td>%s</td>"
					+ "<td>%s</td>"
					+ "<td>%s</td>",
					user.getUserId(), user.getName(), user.getEmail())
				);

				stringBuilder.append("		</tr>");
			}
			stringBuilder.append("	</tbody>");

			stringBuilder.append("</table>");

			byte[] body = stringBuilder.toString()
				.getBytes();

			HttpResponseHeader header = new HttpResponseHeader(HttpStatus.OK);
			header.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length));
			header.putHeader(HttpHeaders.CONTENT_TYPE, HttpContentType.TEXT_HTML.getContentType());
			return new HttpResponse(header, body);
		}

		throw new IllegalStateException(
			String.format("Cannot handle request. http request = %s", httpRequest)
		);
	}

	private HttpResponse responseNotLogined() {
		HttpResponseHeader header = new HttpResponseHeader(HttpStatus.FOUND);
		header.putHeader(HttpHeaders.CONTENT_LENGTH, "0");
		header.putHeader("Location", "/user/login_failed.html");
		header.addCookie("logined", "false");
		header.addCookiePath("/");
		return new HttpResponse(header);
	}

	private static boolean isNotLogined(String logined) {
		return logined == null || !logined.equals("true");
	}

	private boolean isGetUserListRequest(HttpRequest httpRequest) {
		return httpRequest.getHttpMethod() == HttpMethod.GET &&
			httpRequest.getRequestUri()
				.equals("/user/list");
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

	private void createUser(Map<String, String> params) {
		User user = new User(
			params.get("userId"),
			params.get("password"),
			params.get("name"),
			params.get("email")
		);

		DataBase.addUser(user);

		log.info("User is created. user = {}", user);
	}

	private boolean login(Map<String, String> params) {
		User user = DataBase.findUserById(
			params.get("userId")
		);

		if (user == null) {
			return false;
		}

		return user.login(params.get("password"));
	}

	private List<User> getUsers() {
		return new ArrayList<>(DataBase.findAll());
	}
}

