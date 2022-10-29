package netlogic.demo.spring.web.example.controllers;

import netlogic.demo.spring.annotation.Bean;
import netlogic.demo.spring.web.annotation.Controller;
import netlogic.demo.spring.web.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Bean
public class HelloController {
    @RequestMapping("/hello")
    public void hello(HttpServletRequest req, HttpServletResponse resp){
        //super.doGet(req, resp);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            resp.getWriter().println("{ \"status\": \"ok\"}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
