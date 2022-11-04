package netlogic.demo.spring.boot;

import netlogic.demo.spring.config.BeanConfig;
import netlogic.demo.spring.context.Context;

public class Application {
    private Context applicationContext;
    private BeanConfig applicationConfig;

    public Application() {
        applicationConfig = new BeanConfig("classpath:/application.properties");
        applicationConfig.buildContext();
        applicationContext = applicationConfig.getContext();
    }

    public BeanConfig getApplicationConfig() {
        return applicationConfig;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }
}
