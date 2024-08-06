package pro.wuan.common.core.utils;

import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 */
public class ReflectionUtils {

    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap<>(256);
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }
    
    
    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters     : 父类中的方法参数
     * @return 父类中方法的执行结果
     */
    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] parameters) {
        //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        //抑制Java对方法进行检查,主要是针对私有方法而言
        method.setAccessible(true);

        try {
            if (null != method) {
                //调用object 的 method 所代表的方法，其方法的参数是 parameters
                return method.invoke(object, parameters);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }
    
    /**
     * 
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getDeclaredFileld(Class clazz,String fieldName) {
    	Field field = null;
    	for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
    	return null;
    }

    /**
     * 根据注解获取字段
     * @param objClass
     * @param annotationClass
     * @return
     */
    public static List<Field> getDeclareFieldByAnnotation(Class objClass, Class annotationClass){
        Field[] fields = objClass.getDeclaredFields();
        List<Field> rtnList = new ArrayList<>();
        if(fields != null){
            for (Field field: fields) {
                if("".equals(field.getName())){

                }
                Annotation[] annotations = field.getDeclaredAnnotations();

                if(field.isAnnotationPresent(annotationClass)){
                    rtnList.add(field);
                }
            }
        }
        return rtnList;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value     : 将要设置的值
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName);
        try {
            //将 object 中 field 所代表的值 设置为 value
            if(field != null){
                //抑制Java对其的检查
                field.setAccessible(true);
                field.set(object, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */
    public static Object getFieldValue(Object object, String fieldName) {
    	//根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName);
        if(field != null){
            //抑制Java对其的检查
            field.setAccessible(true);
            try {
                //获取 object 中 field 所代表的属性值
                return field.get(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
    
    
    /**
     * 
     * @param clazz
     * @param fileName
     * @return
     */
    public static String getFieldType(Class clazz,String fileName) {
    	Field field = getDeclaredFileld(clazz,fileName);
    	return field.getGenericType().toString();
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, (result.length == 0 ? EMPTY_FIELD_ARRAY : result));
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

}
