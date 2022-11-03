package netlogic.demo.spring.bean;

import netlogic.demo.spring.context.Context;
@FunctionalInterface
public interface BeanExtractor {
    public Object extract(Context context);
}
