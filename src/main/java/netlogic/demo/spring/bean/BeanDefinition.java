package netlogic.demo.spring.bean;

import netlogic.demo.spring.annotation.Autowired;
import netlogic.demo.spring.annotation.PostConstruct;
import netlogic.demo.spring.annotation.Value;
import netlogic.demo.spring.context.Context;
import netlogic.demo.spring.util.SpringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Bean定义核心类。在此框架中，不管是通过扫描标注为@Bean的类还是在JavaConfig中配置的Bean，都会先生成BeanDefinition。
 */
public class BeanDefinition {
    //保持所有此Bean依赖的Bean的名字
    private final List<String> dependencies = new ArrayList<>();
    //Bean名称（在context中的key）
    private String name;
    //Bean对应类型
    private Class<?> type;
    //用于生成Bean实例的构造函数
    private Constructor constructor;
    //用于生成Bean实例的工厂函数
    private Supplier<?> beanCreator;

    //所有标注为@Autowired和@Value的field
    private Map<Field, BeanExtractor> autowiredFields = new HashMap<>();
    //所有标注为@Autowired的方法
    private Map<Method, List<BeanExtractor>> autowiredMethods = new HashMap<>();
    //标注为@PostConstruct的方法
    private List<Method> initMethods = new ArrayList<>();
    //此BeanDefinition生成的实例（单例），一个BeanInstance不为空，表示bean实例已经生成，可以作为被依赖相注入到别的Bean。但其功能不一定
    //完备，不一定能对外提供服务（也就上程序员用）
    private Object beanInstance;
    //标记此BeanDefinition是否执行了@PostConstruct方法，bean已经功能完备，可对外提供服务
    private boolean beanInitialized;
    //标记此BeanDefinition是否已经完成了依赖注入，依赖注入完成，表示bean已经基本完备，可支持调用@PostConstruct方法，但不能保证可对外提供
    //服务
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

    /**
     * 用于在生成JavaConfig中@Bean标注的方法定义的Bean
     *
     * @param name         Bean名称
     * @param clazz        Bean类型
     * @param dependencies 生成Bean的方法参数，假定全部都能在Context中找到
     * @param beanCreator  闭包，包裹生成Bean的方法及参数
     */
    public BeanDefinition(String name, Class<?> clazz, Class<?>[] dependencies, Supplier<?> beanCreator) {
        this(name, clazz);
        this.beanCreator = beanCreator;
        this.dependencies.addAll(Arrays.stream(dependencies).map(c -> SpringUtils.getBeanName(c, c.getName())).toList());
    }

    public String getName() {
        return name;
    }

    public void createInstance(Context context) {
        if (beanInstance == null && beanCreator != null) {//优先采用bean工厂闭包生成Bean
            beanInstance = beanCreator.get();
        }
        if (beanInstance == null && constructor != null) {//如果有标注为@Autowired的有参构造函数，则采用此构造函数生成bean
            beanInstance = SpringUtils.newInstance(constructor, getParameterInjectors(constructor).stream().map(p -> p.extract(context)).toArray());
        }
        createInstance();//默认采用无参构造函数生成bean
    }

    /**
     * 为所以标注为@Autowired和@Value的Field注入对应Bean然后在调用标注为@Autowired的方法
     *
     * @param context
     */
    public void callAutowiredMethods(Context context) {
        if (beanInjectionCompleted) {
            return;
        }
        autowiredFields.forEach((f, dep) -> {
            SpringUtils.setField(f, beanInstance, dep.extract(context));
        });
        autowiredMethods.forEach((m, deps) -> {
            SpringUtils.invoke(m, beanInstance, deps.stream().map(dep -> dep.extract(context)).toArray());
        });
        beanInjectionCompleted = true;
    }

    public void callPostConstruct() {
        if (beanInitialized) {
            return;
        }
        initMethods.forEach(m -> {
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

    /**
     * 获取方法或构造函数参数代表的依赖项名称
     *
     * @param e
     * @return
     */
    private List<String> getParameterDependencies(Executable e) {
        return Arrays.stream(e.getParameterTypes()).map(p -> SpringUtils.getBeanName(p, p.getName())).toList();
    }

    private List<BeanExtractor> getParameterInjectors(Executable e) {
        return Arrays.stream(e.getParameterTypes()).map(p -> {
            String beanName = SpringUtils.getBeanName(p, p.getName());
            if (p.isAnnotationPresent(Autowired.class)) {
                return (BeanExtractor) context -> context.getBean(beanName);
            } else {//@Value
                return ValueExtractors.getValueExtractor(p, beanName);
            }
        }).toList();
    }


    /**
     * 搜集依赖性
     */
    private void init() {
        //@Autowired Field依赖
        autowiredFields = Arrays.stream(this.type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toMap(f -> f, f -> {
                    String name = SpringUtils.getBeanName(f, f.getType().getName());
                    dependencies.add(name);
                    return context -> context.getBean(name);
                }));
        //@Value Field依赖
        Map<Field, BeanExtractor> valueFields = Arrays.stream(this.type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Value.class))
                .collect(Collectors.toMap(f -> f, f -> {
                    String valueExpression = f.getAnnotation(Value.class).value();
                    dependencies.add(valueExpression);
                    return ValueExtractors.getValueExtractor(f.getType(), valueExpression);
                }));
        autowiredFields.putAll(valueFields);
        //@Autowired方法的参数依赖
        autowiredMethods = Arrays.stream(this.type.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Autowired.class))
                .collect(Collectors.toMap(m -> m, m -> {
                    dependencies.addAll(getParameterDependencies(m));
                    return getParameterInjectors(m);
                }));
        initMethods = Arrays.stream(this.type.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(PostConstruct.class))
                .toList();
        if (constructor == null) {
            Arrays.stream(this.type.getDeclaredConstructors())
                    .filter(c -> c.isAnnotationPresent(Autowired.class))
                    .findFirst()
                    .ifPresent(c -> {
                        dependencies.addAll(getParameterDependencies(c));
                        constructor = c;
                    });
        }
    }
}
