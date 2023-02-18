package Handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.HttpRequest;

public class UserHandler implements Handler {
	private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
	@Override
	public boolean isPossible(HttpRequest httpRequest) {
		return httpRequest.getRequestUri()
			.startsWith("/user");
	}

	public String createUser(Map<String, String> params) {
		User user = new User(
			params.get("userId"),
			params.get("password"),
			params.get("name"),
			params.get("email")
		);

		DataBase.addUser(user);

		log.info("User is created. user = {}", user);

		return user.toString();
	}

}
