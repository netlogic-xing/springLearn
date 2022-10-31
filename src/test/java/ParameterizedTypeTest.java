import netlogic.demo.spring.web.annotation.RequestBody;
import netlogic.demo.spring.web.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParameterizedTypeTest {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        Method[] methods = ParameterizedTypeTest.class.getDeclaredMethods();
//        ParameterizedType pt = (ParameterizedType) (Type)(methods[1].getParameterTypes()[0]);
        System.out.println("");
    }
    public @ResponseBody List<Map<String, User>> show(List<User> list, int a){
        System.out.println(list);
        return null;
    }
}
