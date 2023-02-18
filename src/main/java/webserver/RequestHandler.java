package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Handler.ResourceHandler;
import Handler.UserHandler;
import com.google.common.net.MediaType;
import util.HttpRequestUtils;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            log.info("HttpRequest: {}", httpRequest);

            byte[] body = "Hello World".getBytes();
            if (resourceHandler.isPossible(httpRequest)) {
                body = resourceHandler.serve(httpRequest.getRequestUri());
                if (resourceHandler.isCssRequest(httpRequest)) {
                    DataOutputStream dos = new DataOutputStream(out);
                    response200HeaderWithCss(dos, body.length);
                    responseBody(dos, body);


                }

            } else if (userHandler.isPossible(httpRequest)) {

                if (userHandler.isCreateUserRequest(httpRequest)) {
                    userHandler.createUser(
                        HttpRequestUtils.parseQueryString(
                            httpRequest.getHttpBody()
                        )
                    );

                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos);
                    responseBody(dos, body);
                    return;
                } else if (userHandler.isLoginRequest(httpRequest)) {
                    boolean success = userHandler.login(
                        HttpRequestUtils.parseQueryString(
                            httpRequest.getHttpBody()
                        )
                    );

                    if (!success) {
                        body = resourceHandler.serve("/user/login_failed.html");
                        DataOutputStream dos = new DataOutputStream(out);
                        response401Header(dos, body.length);
                        responseBody(dos, body);
                    } else {
                        DataOutputStream dos = new DataOutputStream(out);
                        response302HeaderWithCookie(dos, Map.of("logined", "true"));
                        responseBody(dos, body);
                    }
                }
            }

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ MediaType.CSS_UTF_8 + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, Map<String, String> cookies) {
        try {
            StringBuilder stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                stringBuilder.append(
                        entry.getKey()
                    ).append("=")
                    .append(entry.getValue());
            }

            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("Set-Cookie: " + stringBuilder + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response401Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 401 Unauthorized \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("WWW-Authenticate: Basic realm=\"Input Correct credentials\"" + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
