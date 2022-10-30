package netlogic.demo.spring.web.base;

import netlogic.demo.spring.web.ControllerConfigException;
import netlogic.demo.spring.web.RequestHandleException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {
    private Method method;
    private Object target;

    private List<ParameterValueHolder> parameterValueHolders;

    public RequestHandler(Object target, Method method) {
        this.method = method;
        this.target = target;
        parameterValueHolders = Arrays.stream(method.getParameters()).map(ParameterValueHolder::new).toList();
    }


    public void handle(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) {
        try {
            parseParameters(req, resp, matcher);
            method.invoke(target, parameterValueHolders.stream().map(p -> p.getValue()).toArray());
        } catch (IllegalAccessException e) {
            throw new RequestHandleException(e);
        } catch (InvocationTargetException e) {
            throw new RequestHandleException(e);
        }
    }

    private void parseParameters(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) {
        parameterValueHolders.stream().filter(p->p.isRequest()).forEach(p->{
            p.setValue(req);
        });
        parameterValueHolders.stream().filter(p->p.isResponse()).forEach(p->{
            p.setValue(resp);
        });
        parameterValueHolders.stream().filter(p -> p.isPathVariable()).forEach(p -> {
            p.setValue(matcher.group(p.getName()));
        });
        parameterValueHolders.stream().filter(p -> p.isRequestParameter()).forEach(p -> {
            p.setValue(req.getHeader(p.getName()));
        });
        parameterValueHolders.stream().filter(p -> p.isRequestParameter()).forEach(p -> {
            p.setValue(req.getParameter(p.getName()));
        });

    }
}
