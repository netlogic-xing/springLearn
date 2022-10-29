package netlogic.demo.spring.web;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import netlogic.demo.spring.config.BeanConfig;
import netlogic.demo.spring.web.annotation.Controller;
import netlogic.demo.spring.web.annotation.RequestMapping;
import netlogic.demo.spring.web.annotation.WebConfiguration;
import netlogic.demo.spring.web.base.RequestHandler;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class DispatchServlet extends HttpServlet {
    private BeanConfig beanConfig;
    private Table<String, String, RequestHandler> controllerMethods;

    private String path(HttpServletRequest req) {
        String contextPath = Strings.nullToEmpty(req.getContextPath());
        String uri = req.getRequestURI();
        return uri.substring(uri.indexOf(contextPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        controllerMethods.get("GET", path(req)).handle(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doHead(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    public void init() throws ServletException {
        Reflections reflections = new Reflections(ClasspathHelper.forJavaClassPath());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(WebConfiguration.class);
        if (classes.isEmpty()) {
            throw new WebConfigNotFoundException(getServletName());
        }
        Class<?> webConfigClass = classes.stream().findFirst().orElseThrow(() -> new WebConfigNotFoundException("No class annotated by @WebConfiguration Found!"));
        log("Using web config class: " + webConfigClass.getName());
        beanConfig = new BeanConfig(webConfigClass);
        controllerMethods = HashBasedTable.create();
        List<?> controllers = beanConfig.getContext().getBeansByAnnotation(Controller.class);
        controllers.stream().forEach(controller -> {
            RequestMapping baseMapping = controller.getClass().getAnnotation(RequestMapping.class);
            final String baseUri = baseMapping == null ? "" : Strings.nullToEmpty(baseMapping.value());
            ReflectionUtils.getMethods(controller.getClass(), m -> m.isAnnotationPresent(RequestMapping.class)).forEach(m -> {
                RequestMapping methodMapping = m.getAnnotation(RequestMapping.class);
                controllerMethods.put(methodMapping.method(), baseUri + methodMapping.value(), args -> {
                    try {
                        return m.invoke(controller, args);
                    } catch (Exception e) {
                        throw new RequestHandleException(e);
                    }
                });
            });

        });
    }
}
