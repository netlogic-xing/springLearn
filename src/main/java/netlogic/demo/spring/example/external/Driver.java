package netlogic.demo.spring.example.external;

public class Driver {
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                '}';
    }

    public Driver(String name) {
        this.name = name;
    }
}
