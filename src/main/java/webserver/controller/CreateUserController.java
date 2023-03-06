package webserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class CreateUserController extends AbstractController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);

	@Override
	void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
		throw new UnsupportedOperationException();
	}

	@Override
	void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
		String userId = httpRequest.getRequestParameter("userId");
		String password = httpRequest.getRequestParameter("password");
		String name = httpRequest.getRequestParameter("name");
		String email = httpRequest.getRequestParameter("email");

		User user = new User(
			userId
			, password
			, name
			, email
		);

		LOGGER.debug("User is created. user = {}", user);

		DataBase.addUser(user);
		httpResponse.sendRedirect("/index.html");
	}
}
