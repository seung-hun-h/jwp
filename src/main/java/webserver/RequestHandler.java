package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.handler.ResourceHandler;
import webserver.handler.UserHandler;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final ResourceHandler resourceHandler;
    private final UserHandler userHandler;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.resourceHandler = new ResourceHandler();
        this.userHandler = new UserHandler();
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

            if (resourceHandler.isPossible(httpRequest)) {
                resourceHandler.handle(httpRequest, httpResponse);
                httpResponse.flush();
                return;
            }

            if (userHandler.isPossible(httpRequest)) {
                userHandler.handle(httpRequest, httpResponse);
                httpResponse.flush();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
