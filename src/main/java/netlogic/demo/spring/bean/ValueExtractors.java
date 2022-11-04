package netlogic.demo.spring.bean;

import java.util.Optional;
import java.util.function.Function;

/**
 * ValueExtractor工厂
 */
public class ValueExtractors {
    /**
     * 可根据类型对值进行转换
     *
     * @param clazz
     * @param valueExpression
     * @return
     */
    public static BeanExtractor getValueExtractor(Class<?> clazz, String valueExpression) {
        return context -> {
            String value = (String) context.getBean(valueExpression);
            if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
                return convert(value, Integer::parseInt);
            }
            if (short.class.isAssignableFrom(clazz) || Short.class.isAssignableFrom(clazz)) {
                return convert(value, Short::parseShort);
            }
            if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
                return convert(value, Long::parseLong);
            }
            if (float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
                return convert(value, Float::parseFloat);
            }
            if (double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
                return convert(value, Double::parseDouble);
            }
            if (byte.class.isAssignableFrom(clazz) || Byte.class.isAssignableFrom(clazz)) {
                return convert(value, Byte::parseByte);
            }
            if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
                return convert(value, Boolean::parseBoolean);
            }
            if (char.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)) {
                return convert(value, v -> v.charAt(0));
            }
            return value;
        };
    }

    private static Object convert(String value, Function<String, Object> converter) {
        Optional<String> optionalValue = Optional.ofNullable(value);
        return optionalValue.map(converter).orElse(null);
    }
}
