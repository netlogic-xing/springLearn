package netlogic.demo.spring.web.base;

@FunctionalInterface
public interface RequestHandler {
    public Object handle(Object...args);
}
