package webserver.controller;

import java.util.Collection;

import db.DataBase;
import model.User;
import webserver.http.Cookie;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class ListUserController extends AbstractController {
	private boolean isNotLogined(HttpRequest httpRequest) {
		Cookie cookie = httpRequest.getCookie("logined");

		return cookie == null || !cookie.getValue().equals("true");
	}

	@Override
	void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
		if (isNotLogined(httpRequest)) {
			Cookie cookie = new Cookie("logined", "false");
			cookie.addPath("/");
			httpResponse.addCookie(cookie);
			httpResponse.sendRedirect("/user/login_failed.html");
		}

		Collection<User> users = DataBase.findAll();

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

		httpResponse.forwardBody(stringBuilder.toString());
	}

	@Override
	void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
		throw new UnsupportedOperationException();
	}
}
