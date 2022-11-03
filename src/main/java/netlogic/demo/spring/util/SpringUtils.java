package netlogic.demo.spring.util;

import com.google.common.base.Strings;
import jdk.dynalink.beans.StaticClass;
import netlogic.demo.spring.BeanInitializationException;
import netlogic.demo.spring.annotation.Autowired;
import netlogic.demo.spring.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.util.Set;
import java.util.stream.Collectors;

public class SpringUtils {
    public static String getBeanName(AnnotatedElement p, String defaultName) {
        String name = null;
        Autowired autowired = p.getAnnotation(Autowired.class);
        if (autowired != null && autowired.name() != null && !autowired.name().equals("")) {
            name = autowired.name();
        }
        Value value = p.getAnnotation(Value.class);
        if(value != null && !Strings.isNullOrEmpty(value.value())){
            name = value.value();
        }
        if (name == null) {
            name = defaultName;
        }
        return name;
    }

    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInitializationException(e);
        }
    }

    public static Object newInstance(Constructor constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new BeanInitializationException(e);
        }
    }

    public static Object invoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new BeanInitializationException(e);
        }
    }

    public static void setField(Field field, Object target, Object value){
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
             throw new BeanInitializationException(e);
        }
    }
    //此方法可用Reflections改造
    public static Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }
}
