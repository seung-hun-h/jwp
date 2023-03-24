package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.controller.Controller;
import webserver.controller.CreateUserController;
import webserver.controller.ListUserController;
import webserver.controller.LoginController;
import webserver.http.Cookie;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpSession;
import webserver.http.HttpSessions;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Map<String, Controller> handlerMapping = new HashMap<>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;

        handlerMapping.put("/user/create", new CreateUserController());
        handlerMapping.put("/user/list", new ListUserController());
        handlerMapping.put("/user/login", new LoginController());
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = HttpRequest.parse(in);
            HttpResponse httpResponse = new HttpResponse(out);

            log.info("HttpRequest: {}", httpRequest);

            if (httpRequest.getCookie("JSESSIONID") == null) {
                Cookie cookie = new Cookie("JSESSIONID", UUID.randomUUID().toString());
                httpResponse.addCookie(cookie);
            }

            String requestUri = httpRequest.getRequestUri();
            Controller controller = handlerMapping.get(requestUri);

            if (controller == null) {
                httpResponse.forward(getDefaultUri(requestUri));
                return;
            }

            controller.service(httpRequest, httpResponse);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultUri(String uri) {
        if (uri.equals("/")) {
            return "/index.html";
        }
        return uri;
    }

}
