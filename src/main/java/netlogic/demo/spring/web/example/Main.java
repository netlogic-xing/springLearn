package netlogic.demo.spring.web.example;

import netlogic.demo.spring.WebApplication;
import netlogic.demo.spring.annotation.Configuration;
import netlogic.demo.spring.web.DispatchServlet;
import netlogic.demo.spring.boot.JettyServer;
import netlogic.demo.spring.web.annotation.WebConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.Set;
@WebConfiguration
@Configuration(scanPackages = "netlogic.demo.spring.web.example.controllers")
public class Main extends WebApplication {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.start();
    }
}
