package netlogic.demo.spring.iocexample.service;

import netlogic.demo.spring.annotation.Autowired;
import netlogic.demo.spring.annotation.Bean;
import netlogic.demo.spring.annotation.PostConstruct;
import netlogic.demo.spring.iocexample.external.DataSource;

@Bean
public class HostService {
    @Autowired
    private DataSource dataSource;

    public void doAction() {
        System.out.println(dataSource);
    }

    @PostConstruct
    public void show() {
        System.out.println("post construct!");
    }
}
