package netlogic.demo.spring.config;

import netlogic.demo.spring.annotation.Bean;
import netlogic.demo.spring.annotation.Configuration;
import netlogic.demo.spring.annotation.Import;
import netlogic.demo.spring.bean.BeanDefinition;
import netlogic.demo.spring.context.Context;
import netlogic.demo.spring.util.SpringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanConfig {
    /**
     * All beans defined here
     */
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    private Context context = new Context();

    public BeanConfig(Class<?> configClass) {
        // Add bean defined within the config class
        beanDefinitions.putAll(buildBeanDefinitionsFromConfigClass(configClass));


        Configuration configuration = configClass.getAnnotation(Configuration.class);
        if (configuration == null) {
            return;
        }
        //Add bean from package
        Arrays.stream(configuration.scanPackages()).map(p -> buildBeanDefinitionsFromPackage(p)).forEach(bd -> {
            beanDefinitions.putAll(bd);
        });
        Import importConfigs = configClass.getAnnotation(Import.class);
        if (importConfigs != null) {
            // Add bean imported from imported config
            Arrays.stream(importConfigs.value()).forEach(importConfigClass -> {
                beanDefinitions.putAll(buildBeanDefinitionsFromConfigClass(importConfigClass));
            });
        }
        createBeans(beanDefinitions.values().stream().toList());
        beansInject(beanDefinitions.values().stream().toList());
        beansPostConstruct(beanDefinitions.values().stream().toList());
    }

    private void createBeans(List<BeanDefinition> currentBds) {
        currentBds.forEach(bd -> {
            createBeans(bd.getDependencies().stream().map(name -> beanDefinitions.get(name)).toList());
            bd.createInstance(context);
            context.addBean(bd);
        });
    }

    private void beansInject(List<BeanDefinition> currentBds) {
        currentBds.forEach(bd -> {
            beansInject(bd.getDependencies().stream().map(name -> beanDefinitions.get(name)).toList());
            bd.callAutowiredMethods(context);
        });
    }

    private void beansPostConstruct(List<BeanDefinition> currentBds) {
        currentBds.forEach(bd -> {
            beansPostConstruct(bd.getDependencies().stream().map(name -> beanDefinitions.get(name)).toList());
            bd.callPostConstruct();
        });
    }

    private Map<String, BeanDefinition> buildBeanDefinitionsFromPackage(String packageName) {
        return SpringUtils.findAllClassesUsingClassLoader(packageName).stream()
                .filter(c -> c.isAnnotationPresent(Bean.class))
                .map(c -> {
                    String name = c.getName();
                    Bean bean = c.getAnnotation(Bean.class);
                    if (bean != null && !bean.name().equals("")) {
                        name = bean.name();
                    }
                    return new BeanDefinition(name, c);
                })
                .collect(Collectors.toMap(b -> b.getName(), b -> b));
    }

    private Map<String, BeanDefinition> buildBeanDefinitionsFromConfigClass(Class<?> configClass) {
        BeanDefinition root = new BeanDefinition(configClass);
        root.createInstance();
        Map<String, BeanDefinition> bd = Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> {
                    return m.isAnnotationPresent(Bean.class);
                })
                .map(m -> {
                    String name = m.getAnnotation(Bean.class).name();
                    if (name == null || name.equals("")) {
                        name = m.getReturnType().getName();
                    }
                    return new BeanDefinition(name, m.getReturnType(), m.getParameterTypes(), () -> {
                        return SpringUtils.invoke(m, root.getBeanInstance(), context.getBeans(m.getParameterTypes()));
                    });
                }).collect(Collectors.toMap(b -> b.getName(), b -> b));
        bd.put(root.getName(), root);
        return bd;
    }

    public Context getContext() {
        return context;
    }
}
