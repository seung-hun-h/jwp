package tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class ServletApplication {
	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		Connector connector = new Connector();
		connector.setPort(8081);
		tomcat.setConnector(connector);

		Context context = tomcat.addContext("", "/");
		tomcat.addServlet("", "helloServlet", new HelloServlet());
		context.addServletMappingDecoded("/hello-servlet", "helloServlet");
		tomcat.start();
	}
}
