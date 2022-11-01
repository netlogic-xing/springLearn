package netlogic.demo.spring.web.example;

import netlogic.demo.spring.WebApplication;
import netlogic.demo.spring.annotation.Bean;
import netlogic.demo.spring.annotation.Configuration;
import netlogic.demo.spring.web.DispatchServlet;
import netlogic.demo.spring.boot.JettyServer;
import netlogic.demo.spring.web.annotation.WebConfiguration;
import netlogic.demo.spring.web.annotation.WebInit;
import netlogic.demo.spring.web.view.ThymeleafViewRenderer;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import javax.servlet.ServletContext;
import java.util.Set;
@WebConfiguration
@Configuration(scanPackages = "netlogic.demo.spring.web.example.controllers")
public class Main extends WebApplication {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.start();
    }
    @Bean
    public ThymeleafViewRenderer thymeleafViewRenderer(ServletContext servletContext){
        return new ThymeleafViewRenderer(servletContext);
    }
    @WebInit
    public void initWeb(){

    }
}
