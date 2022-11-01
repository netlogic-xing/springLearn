package netlogic.demo.spring.context;

import netlogic.demo.spring.BeanNotFoundException;
import netlogic.demo.spring.bean.BeanDefinition;
import netlogic.demo.spring.util.SpringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean容器类
 */
public class Context {
    private Map<String, Object> beans = new HashMap<>();

    public Object getBean(String name) {
        return this.beans.get(name);
    }
    public List<?> getBeansByAnnotation(Class<? extends Annotation> annotation){
        return beans.values().stream().filter(o-> o.getClass().isAnnotationPresent(annotation)).toList();
    }
    public void addBean(BeanDefinition bd){
        beans.put(bd.getName(), bd.getBeanInstance());
    }
    public Object getBeanByType(Class<?> clazz) {
        return this.beans.get(clazz.getName());
    }

    public Object getBean(Class<?> clazz) {
        return getBean(SpringUtils.getBeanName(clazz, clazz.getName()));
    }

    public Object[] getBeans(Class<?>... classes) {
        List<Object> targetBeans = new ArrayList<>();
        for (Class<?> clazz : classes) {
            Object bean = getBean(clazz);
            if(bean == null){
                throw new BeanNotFoundException(clazz.getName());
            }
            targetBeans.add(bean);
        }
        return targetBeans.toArray();
    }
    public Object[] getBeans(List<Class<?>> classes){
        return getBeans(classes.toArray(new Class<?>[0]));
    }
    public Object[] getBeans(String... deps) {
        List<Object> targetBeans = new ArrayList<>();
        for (String dep : deps) {
            Object bean = getBean(dep);
            if(bean == null){
                throw new BeanNotFoundException(dep);
            }
            targetBeans.add(bean);
        }
        return targetBeans.toArray();
    }
}
