package netlogic.demo.spring.web.base;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import netlogic.demo.spring.config.BeanConfig;
import netlogic.demo.spring.web.ControllerConfigException;
import netlogic.demo.spring.web.WebConfigNotFoundException;
import netlogic.demo.spring.web.annotation.Controller;
import netlogic.demo.spring.web.annotation.RequestMapping;
import netlogic.demo.spring.web.annotation.WebConfiguration;
import netlogic.demo.spring.web.base.BeanContextHolder;
import netlogic.demo.spring.web.base.RequestHandler;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatchServlet extends HttpServlet {
    private BeanConfig beanConfig;
    private Table<String, Pattern, RequestHandler> controllerMethods;

    private String path(HttpServletRequest req) {
        String contextPath = Strings.nullToEmpty(req.getContextPath());
        String uri = req.getRequestURI();
        return uri.substring(uri.indexOf(contextPath));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAction("GET", req, resp);
    }

    private void doAction(String method, HttpServletRequest req, HttpServletResponse resp) {
        for (Map.Entry<Pattern, RequestHandler>  e: controllerMethods.row(method).entrySet()) {
            Matcher matcher = e.getKey().matcher(path(req));
            if(!matcher.matches()){
                continue;
            }
            e.getValue().handle(req, resp, matcher);
        }
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAction("HEAD", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAction("POST", req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
        doAction("PUT", req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAction("DELETE", req, resp);
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
        //????????????servletConfig???servletContext
        beanConfig.addBean(ServletConfig.class.getName(), this.getServletConfig());
        beanConfig.addBean(ServletContext.class.getName(), this.getServletContext());
        beanConfig.buildContext();
        //???BeanContext??????????????????????????????????????????
        BeanContextHolder.setContext(beanConfig.getContext());
        controllerMethods = HashBasedTable.create();
        List<?> controllers = beanConfig.getContext().getBeansByAnnotation(Controller.class);
        controllers.stream().forEach(controller -> {
            RequestMapping baseMapping = controller.getClass().getAnnotation(RequestMapping.class);
            final String baseUri = baseMapping == null ? "" : Strings.nullToEmpty(baseMapping.value());
            ReflectionUtils.getMethods(controller.getClass(), m -> m.isAnnotationPresent(RequestMapping.class)).forEach(m -> {
                RequestMapping methodMapping = m.getAnnotation(RequestMapping.class);
                if (Strings.isNullOrEmpty(methodMapping.value())) {
                    throw new ControllerConfigException("@RequestMapping has no valid value for method " + m.getName() + " of " + controller.getClass().getName());
                }
                controllerMethods.put(methodMapping.method(), convertUriPatternToRegex(baseUri + methodMapping.value()), new RequestHandler(controller, m));
            });

        });
    }

    private Pattern convertUriPatternToRegex(String uriPattern) {
    StringBuilder uriRePattern = new StringBuilder();
        int begin,end = 0;
        while ((begin = uriPattern.indexOf('{', end)) != -1) {

            uriRePattern.append(uriPattern.substring(end, begin));
            end = uriPattern.indexOf('}', begin) + 1;
            if (end == 0) {
                throw new ControllerConfigException("@RequestMapping has no valid value for method :" + uriPattern);
            }
            String name = uriPattern.substring(begin + 1, end - 1);
            uriRePattern.append("(?<" + name + ">[^/]+)");
        }
        uriRePattern.append(uriPattern.substring(end));
        return Pattern.compile(uriRePattern.toString());
    }
}
