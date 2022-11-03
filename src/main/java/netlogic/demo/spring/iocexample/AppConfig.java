package netlogic.demo.spring.iocexample;

import netlogic.demo.spring.annotation.Bean;
import netlogic.demo.spring.annotation.Configuration;
import netlogic.demo.spring.iocexample.external.DataSource;
import netlogic.demo.spring.iocexample.external.Driver;

@Configuration(scanPackages = {"netlogic.demo.spring.iocexample.model","netlogic.demo.spring.iocexample.service"}, properties = "classpath:/application.properties")
public class AppConfig {

    @Bean
    public Driver driver(){
        return new Driver("dr1");
    }

    @Bean
    public DataSource dataSource(Driver driver){
        DataSource ds = new DataSource("DS1");
        ds.setDriver(driver);
        return ds;
    }
}
