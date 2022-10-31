package netlogic.demo.spring.bean;

import netlogic.demo.spring.annotation.Autowired;
import netlogic.demo.spring.annotation.PostConstruct;
import netlogic.demo.spring.BeanInitializationException;
import netlogic.demo.spring.annotation.Value;
import netlogic.demo.spring.context.Context;
import netlogic.demo.spring.util.SpringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeanDefinition {
    private final List<String> dependencies = new ArrayList<>();
    private String name;
    private Class<?> type;
    private Constructor constructor;

    private Supplier<?> beanCreator;
    private Map<Field, String> autowiredFields = new HashMap<>();
    private Map<Method, List<String>> autowiredMethods = new HashMap<>();
    private List<Method> initMethods = new ArrayList<>();
    private Object beanInstance;

    private boolean beanInitialized;

    private boolean beanInjectionCompleted;

    public List<String> getDependencies() {
        return dependencies;
    }

    public BeanDefinition(String name, String className) throws ClassNotFoundException {
        this.type = Class.forName(className);
        this.name = name;
        init();
    }

    public BeanDefinition(String className) throws ClassNotFoundException {
        this(className, className);
    }

    public BeanDefinition(Class<?> clazz) {
        this(clazz.getName(), clazz);
    }

    public BeanDefinition(String name, Class<?> clazz) {
        this.name = name;
        this.type = clazz;
        init();
    }

    public BeanDefinition(String name, Class<?> clazz, Class<?>[] dependencies, Supplier<?> beanCreator) {
        this(name, clazz);
        this.beanCreator = beanCreator;
        this.dependencies.addAll(Arrays.stream(dependencies).map(c->SpringUtils.getBeanName(c, c.getName())).toList());
    }

    public String getName() {
        return name;
    }

    public void createInstance(Context context) {
        if(beanInstance == null&&beanCreator != null){
            beanInstance = beanCreator.get();
        }
        if (beanInstance == null && constructor != null) {
            beanInstance = SpringUtils.newInstance(constructor, context.getBeans(constructor.getParameterTypes()));
        }
        createInstance();
    }
    public void callAutowiredMethods(Context context){
        if(beanInjectionCompleted){
           return;
        }
        autowiredFields.forEach((f,dep)->{
            SpringUtils.setField(f, beanInstance, context.getBean(dep));
        });
        autowiredMethods.forEach((m,deps)->{
            SpringUtils.invoke(m,beanInstance, context.getBeans(deps.toArray(new String[0])));
        });
        beanInjectionCompleted = true;
    }

    public void callPostConstruct(){
        if(beanInitialized){
            return;
        }
       initMethods.forEach(m->{
           SpringUtils.invoke(m, beanInstance);
       });
       beanInitialized = true;
    }

    public void createInstance() {
        if (beanInstance == null) {
            beanInstance = SpringUtils.newInstance(type);
        }
    }

    public Object getBeanInstance() {
        return beanInstance;
    }

    private List<String> getParameterDependencies(Executable e) {
        return Arrays.stream(e.getParameterTypes()).map(p -> SpringUtils.getBeanName(p, p.getName())).toList();
    }

    private void init() {
        autowiredFields = Arrays.stream(this.type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toMap(f -> f, f -> SpringUtils.getBeanName(f, f.getType().getName())));
        Map<Field, String> valueFields = Arrays.stream(this.type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Value.class))
                .collect(Collectors.toMap(f -> f, f -> f.getAnnotation(Value.class).value()));
        autowiredFields.putAll(valueFields);
        autowiredMethods = Arrays.stream(this.type.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toMap(m -> m, m -> getParameterDependencies(m)));
        initMethods = Arrays.stream(this.type.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(PostConstruct.class))
                .toList();
        if (constructor == null) {
            Arrays.stream(this.type.getDeclaredConstructors())
                    .filter(c -> c.isAnnotationPresent(Autowired.class))
                    .findFirst()
                    .ifPresent(c -> {
                        constructor = c;
                    });
        }

        dependencies.addAll(autowiredFields.values());
        dependencies.addAll(autowiredMethods.values().stream().flatMap(Collection::stream).toList());

        if (constructor != null) {
            dependencies.addAll(getParameterDependencies(constructor));
        }
    }
}
