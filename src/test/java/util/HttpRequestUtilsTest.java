package util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {

    @Test
    void parseQueryStringWhenPasswordIsNull() {
        String queryString = "userId=javajigi";

        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);

        assertEquals(parameters.get("userId"), "javajigi");
        assertNull(parameters.get("password"));
    }

    @Test
    void parseQueryString() {
        String queryString = "userId=javajigi&password=password2";

        Map<String, String>parameters = HttpRequestUtils.parseQueryString(queryString);

        assertEquals(parameters.get("userId"), "javajigi");
        assertEquals(parameters.get("password"), "password2");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void parseQueryStringIsNullOrEmpty(String queryString) {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertTrue(parameters.isEmpty());
    }

    @Test
    void parseQueryStringIsBlank() {
        String queryString = " ";

        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);

        assertTrue(parameters.isEmpty());
    }

    @Test
    void parseQueryStringIsInvalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);


        assertEquals(parameters.get("userId"), "javajigi");
        assertNull(parameters.get("password"));
    }

    @Test
    void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";

        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);

        assertEquals(parameters.get("logined"),"true");
        assertEquals(parameters.get("JSessionId"), "1234");
        assertNull(parameters.get("session"));
    }

    @Test
    void getKeyValue() {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertEquals(pair, new Pair("userId", "javajigi"));
    }

    @Test
    void getKeyValueIsInvalid() {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertNull(pair);
    }

    @Test
    void parseHeader() {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertEquals(pair, new Pair("Content-Length", "59"));
    }
}
