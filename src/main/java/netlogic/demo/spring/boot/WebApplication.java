package netlogic.demo.spring.boot;

import netlogic.demo.spring.boot.JettyServer;
import netlogic.demo.spring.web.base.DispatchServlet;

/**
 * "Springboot"程序启动类基类
 */
public class WebApplication {
    private JettyServer jettyServer;

    public WebApplication() {
        jettyServer = new JettyServer();
        jettyServer.register("/*", DispatchServlet.class);
    }
    public void start(){
        try {
            jettyServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
