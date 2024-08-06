package pro.wuan.common.core.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtils {

    /**
     * 获取泛型参数类型
     * @author ivan
     * @blame
     * @since 2020/9/21
     */
    public static Class getSuperClassGenricType(Class clazz, int index)
            throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }
}
