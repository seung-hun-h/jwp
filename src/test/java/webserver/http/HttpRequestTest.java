package webserver.http;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class HttpRequestTest {
	private static final String TEST_DIRECTORY = "./src/test/resources/";

	@Test
	public void testRequestGet() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(TEST_DIRECTORY + "HTTP_GET.txt"));

		HttpRequest sut = HttpRequest.parse(fileInputStream);

		assertThat(sut.getHttpMethod()).isEqualTo(HttpMethod.GET);
		assertThat(sut.getRequestUri()).isEqualTo("/user/create");
		assertThat(sut.getHeader("Connection")).isEqualTo("keep-alive");
		assertThat(sut.getRequestParameter("userId")).isEqualTo("user");
		assertThat(sut.getRequestParameter("password")).isEqualTo("123");
		assertThat(sut.getRequestParameter("name")).isEqualTo("Seunghun");
	}

	@Test
	public void testRequestPost() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(TEST_DIRECTORY + "HTTP_POST.txt"));

		HttpRequest sut = HttpRequest.parse(fileInputStream);

		assertThat(sut.getHttpMethod()).isEqualTo(HttpMethod.POST);
		assertThat(sut.getRequestUri()).isEqualTo("/user/create");
		assertThat(sut.getRequestParameter("userId")).isEqualTo("user");
		assertThat(sut.getRequestParameter("password")).isEqualTo("123");
		assertThat(sut.getRequestParameter("name")).isEqualTo("Seunghun");
	}
}