package netlogic.demo.spring.iocexample.external;

import netlogic.demo.spring.config.BeanConfig;
import netlogic.demo.spring.iocexample.AppConfig;
import netlogic.demo.spring.iocexample.service.HostService;

public class Application {
    public static void main(String[] args) {
        BeanConfig bc = new BeanConfig(AppConfig.class);
        bc.buildContext();
        HostService service = (HostService) bc.getContext().getBean(HostService.class);
        service.doAction();
    }
}
