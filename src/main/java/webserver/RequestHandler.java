package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Handler.UserHandler;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_RESOURCE_ROOT = System.getProperty("user.dir") + "/webapp";
    private final UserHandler userHandler;

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.userHandler = new UserHandler();
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort()
        );

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            Map<String, String> firstHeaderLine = HttpRequestUtils.getFirstHeaderLine(bufferedReader.readLine());

            String uri = firstHeaderLine.get("uri");

            byte[] body;
            if (uri.startsWith("/user/create")) {
                String queryString = uri.substring(uri.indexOf("?") + 1);
                body = userHandler.createUser(HttpRequestUtils.parseQueryString(queryString))
                    .getBytes();
            } else {
                body = Files.readAllBytes(
                    Path.of(STATIC_RESOURCE_ROOT + uri));
            }

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(createHeaderLine(HttpHeaders.CONTENT_TYPE, MediaType.HTML_UTF_8.type()));
            dos.writeBytes(createHeaderLine(HttpHeaders.CONTENT_LENGTH, String.valueOf(lengthOfBodyContent)));
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

    private String createHeaderLine(String key, String value) {
        return String.format("%s: %s\r\n", key, value);
    }
}
