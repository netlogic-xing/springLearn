package netlogic.demo.spring.example.external;

import netlogic.demo.spring.config.BeanConfig;
import netlogic.demo.spring.example.AppConfig;
import netlogic.demo.spring.example.service.HostService;

public class Application {
    public static void main(String[] args) {
        BeanConfig bc = new BeanConfig(AppConfig.class);
        HostService service = (HostService) bc.getContext().getBean(HostService.class);
        service.doAction();
    }
}
