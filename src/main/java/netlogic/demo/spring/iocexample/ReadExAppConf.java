package netlogic.demo.spring.iocexample;

import netlogic.demo.spring.config.BeanConfig;

public class ReadExAppConf {
    public static void main(String[] args) {
        BeanConfig beanConfig = new BeanConfig(ExAppConf.class);
        beanConfig.buildContext();
    }
}
