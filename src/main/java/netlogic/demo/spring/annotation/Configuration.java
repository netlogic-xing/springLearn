package netlogic.demo.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个JavaConfig类
 */
@Target({
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
    //指定扫描哪些包，这些包下的@Bean标注的类会被自动定义为bean
    String[] scanPackages();
    //指定引入的配置文件，配置文件的内容可以在@Value中使用
    String[] properties() default "";
}
