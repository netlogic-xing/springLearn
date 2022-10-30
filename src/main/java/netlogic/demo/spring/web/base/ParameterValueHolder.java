package netlogic.demo.spring.web.base;

import com.google.common.base.Strings;
import netlogic.demo.spring.web.annotation.PathVariable;
import netlogic.demo.spring.web.annotation.RequestBody;
import netlogic.demo.spring.web.annotation.RequestHeader;
import netlogic.demo.spring.web.annotation.RequestParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

//private ConcurrentMap<> exceptionHandlers;
class ParameterValueHolder {
    private Class<?> type;
    private Object value;
    private String name;
    private boolean pathVariable;
    private boolean requestParameter;
    private boolean requestHeader;
    private boolean requestBody;

    private boolean isRequest;

    private boolean isResponse;

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public ParameterValueHolder(Parameter p) {
        this.type = p.getType();
        this.name = p.getName();
        if (p.isAnnotationPresent(PathVariable.class)) {
            this.setPathVariable(true);
            String val = p.getAnnotation(PathVariable.class).value();
            name = Strings.isNullOrEmpty(val) ? val : name;
            return;
        }
        if(p.isAnnotationPresent(RequestParameter.class)){
           this.setRequestParameter(true);
           String val = p.getAnnotation(RequestParameter.class).value();
            name = Strings.isNullOrEmpty(val) ? val : name;
            return;
        }
        if(p.isAnnotationPresent(RequestHeader.class)){
           this.setRequestHeader(true);
           String val = p.getAnnotation(RequestHeader.class).value();
            name = Strings.isNullOrEmpty(val) ? val : name;
            return;
        }

        if(p.isAnnotationPresent(RequestBody.class)){
            this.setRequestBody(true);
            String val = p.getAnnotation(RequestBody.class).value();
            name = Strings.isNullOrEmpty(val) ? val : name;
            return;
        }
        if(HttpServletRequest.class.isAssignableFrom(p.getType())) {
            this.setRequest(true);
            return;
        }
        if(HttpServletResponse.class.isAssignableFrom(p.getType())){
            this.setResponse(true);
            return;
        }

    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(boolean pathVariable) {
        this.pathVariable = pathVariable;
    }

    public boolean isRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(boolean requestParameter) {
        this.requestParameter = requestParameter;
    }

    public boolean isRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(boolean requestHeader) {
        this.requestHeader = requestHeader;
    }

    public boolean isRequestBody() {
        return requestBody;
    }

    public void setRequestBody(boolean requestBody) {
        this.requestBody = requestBody;
    }
}
