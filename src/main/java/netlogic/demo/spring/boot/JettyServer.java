package netlogic.demo.spring.boot;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

public class JettyServer {
    private Server server;
    private ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

    public void start() throws Exception{
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});
        servletHandler.setContextPath("/");
        servletHandler.setResourceBase("src/main/resources");
        server.setHandler(servletHandler);
        server.start();
    }

    public void register(String mapping, Class<? extends HttpServlet> servletClass){
        servletHandler.addServlet(servletClass, mapping);
    }
}
