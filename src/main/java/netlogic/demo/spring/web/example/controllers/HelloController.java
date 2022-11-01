package netlogic.demo.spring.web.example.controllers;

import netlogic.demo.spring.annotation.Bean;
import netlogic.demo.spring.web.annotation.Controller;
import netlogic.demo.spring.web.annotation.Model;
import netlogic.demo.spring.web.annotation.RequestBody;
import netlogic.demo.spring.web.annotation.RequestMapping;
import netlogic.demo.spring.web.example.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Bean
public class HelloController {
    @RequestMapping("/hello")
    public String hello() {
        return "/hello";
    }

    @RequestMapping("/home")
    public @Model(view = "/home") Map<String, Object> home() {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", "Chenyang");
        user.put("lastName", "Xing");
        return user;
    }

    @RequestMapping(method = "POST", value = "/user")
    public List<User> users(@RequestBody List<User> users) {
        return users;
    }
}
