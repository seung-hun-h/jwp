package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handler.ResourceHandler;
import handler.UserHandler;

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
             OutputStream out = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
             DataOutputStream dataOutputStream = new DataOutputStream(out)
        ) {
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            log.info("HttpRequest: {}", httpRequest);

            if (resourceHandler.isPossible(httpRequest)) {
                HttpResponse httpResponse = resourceHandler.handle(httpRequest);
                response(dataOutputStream, httpResponse);
                return;
            }

            if (userHandler.isPossible(httpRequest)) {
                HttpResponse httpResponse = userHandler.handle(httpRequest);
                response(dataOutputStream, httpResponse);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(DataOutputStream dos, HttpResponse httpResponse) {
        log.info("Http Response: {}", httpResponse);
        try {
            dos.writeBytes(httpResponse.getHttpResponseHeader());
            dos.writeBytes("\r\n");
            dos.write(httpResponse.getHttpBody(), 0, httpResponse.getHttpBody().length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
