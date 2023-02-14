package Handler;

import java.util.Map;

import db.DataBase;
import model.User;

public class UserHandler {

	public String createUser(Map<String, String> params) {
		User user = new User(
			params.get("userId"),
			params.get("password"),
			params.get("name"),
			params.get("email")
		);

		DataBase.addUser(user);

		return user.toString();
	}
}
