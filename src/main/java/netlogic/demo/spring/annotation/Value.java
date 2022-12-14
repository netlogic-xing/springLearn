package netlogic.demo.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于获取properties中的值并给指定field，parameter注入
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
   /**
    * 这里暂时不同于spring的value，这里直接写properties文件中的key，不用${}语法，也不支持表达式
    * @return
    */
   String value();
   String defaultValue() default "";
}
