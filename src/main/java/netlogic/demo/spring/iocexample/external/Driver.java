package netlogic.demo.spring.iocexample.external;

import netlogic.demo.spring.annotation.Value;

public class Driver {
    private String name;
    @Value("spring.server.port")
    private int port;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                "port=" + port +
                '}';
    }

    public Driver(String name) {
        this.name = name;
    }
}
