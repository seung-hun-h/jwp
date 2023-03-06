package webserver.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

class HttpResponseTest {
	private static final String TEST_DIRECTORY = "./src/test/resources/";

	@Test
	void testResponseForward() throws FileNotFoundException {
	    // given
		HttpResponse response = new HttpResponse(createOutputStream("HTTP_Foward.txt"));

		// when
		response.forward("/index.html");

	    // then

	}

	private OutputStream createOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(new File(TEST_DIRECTORY + fileName));
	}
}