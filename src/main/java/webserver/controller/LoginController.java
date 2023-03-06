package webserver.controller;

import db.DataBase;
import model.User;
import webserver.http.Cookie;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class LoginController extends AbstractController {
	@Override
	void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
		throw new UnsupportedOperationException();
	}

	@Override
	void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
		String userId = httpRequest.getRequestParameter("userId");
		String password = httpRequest.getRequestParameter("password");

		User user = DataBase.findUserById(userId);

		if (isLogined(user, password)) {
			Cookie cookie = new Cookie("logined", "true");
			cookie.addPath("/");
			httpResponse.addCookie(cookie);
			httpResponse.sendRedirect("/index.html");
			return;
		}

		Cookie cookie = new Cookie("logined", "false");
		cookie.addPath("/");
		httpResponse.addCookie(cookie);
		httpResponse.sendRedirect("/user/login_failed.html");
	}

	private boolean isLogined(User user, String password) {
		if (user == null) {
			return false;
		}

		return user.login(password);
	}
}
