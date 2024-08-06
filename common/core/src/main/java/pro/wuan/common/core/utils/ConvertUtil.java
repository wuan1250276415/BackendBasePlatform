package pro.wuan.common.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import pro.wuan.common.core.model.BaseQueryValue;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/20 11:10
 */
public class ConvertUtil {

    //求和
    private static String FIELD = "\\$\\{(.*?)\\}";

    public static void main(String[] args) throws JsonProcessingException {
        String ctx = "验证码为${a},${e.f}";
        // 编译表达式
        String json = "{\n" +
                "    \"money\":\"9999\",\n" +
                "    \"a\":\"avar\",\n" +
                "    \"avg\":\"avgvar\",\n" +
                "    \"e\":{\"f\":\"fv\"}\n" +
                "}";
        Map<String, Object> map = ConvertUtil.stringToObject(json, Map.class);
        System.out.println(ConvertUtil.convert(ctx, map));

    }

    //map转java对象
    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) {
        String jsonStr = JSONObject.toJSONString(map);
        return (T) JSONObject.parseObject(jsonStr, beanClass);
    }

    //map转java对象
    public static <T> T queryMapToObject(Map<String, BaseQueryValue> map, Class<T> beanClass) throws Exception {
        if (map == null)
            return null;

        T obj = beanClass.newInstance();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                if (map.containsKey(property.getName())) {
                    if (property.getPropertyType() == Long.class) {
                        String val = String.valueOf(map.get(property.getName()).getValues()[0]);
                        setter.invoke(obj, Long.valueOf(val));
                    } else if (property.getPropertyType() == Integer.class) {
                        String val = String.valueOf(map.get(property.getName()).getValues()[0]);
                        setter.invoke(obj, Integer.valueOf(val));
                    } else {
                        setter.invoke(obj, map.get(property.getName()).getValues()[0]);
                    }
                }
            }
        }
        return obj;
    }


    //java对象转map
    public static Map<String, Object> objectToMap(Object obj) {
        String jsonStr = JSONObject.toJSONString(obj);
        return JSONObject.parseObject(jsonStr);
    }

    public static Map<String, Object> objectToMapWithNullProperty(Object obj) {
        String jsonStr = JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        return JSONObject.parseObject(jsonStr);
    }
    //json转java对象

    /**
     * 默认会返回一个空的对象
     *
     * @param entityJSON
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T stringToObject(String entityJSON, Class<T> beanClass) {
        return StringUtils.isBlank(entityJSON) ? null : (T) JSONObject.parseObject(entityJSON, beanClass);
    }

    //java对象转json
    public static String objectToString(Object obj) {
        return null == obj ? null : JSONObject.toJSONString(obj);
    }

    public static Map<String, Object> stringToMap(String entityJSON) {
        JSONObject jsonObject = JSONObject.parseObject(entityJSON);
        Map<String, Object> jsonMap = JSONObject.toJavaObject(jsonObject, Map.class);
        return jsonMap;
    }

    //原始表达式替换
    public static String replaceExpression(String expression) {
        return expression.replaceAll("\\.", "_");
    }

    //获取字段列表
    public static List<String> getFeild(String reg, String expressionp) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(expressionp);
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }

    //原始表达式参数设置
    public static Map<String, Object> resetExpressionVar(String expression, Map<String, Object> map) {
        String newExperssion = replaceExpression(expression);
        Map<String, Object> newMap = new HashedMap();
        List<String> fields = getFeild(FIELD, newExperssion);
        for (String field : fields) {
            String[] elem = field.split("_");
            if (elem.length > 1) {
                if (map.get(elem[0]) instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) map.get(elem[0]);
                    newMap.put(field, jsonObject.get(elem[1]));
                }
            } else {
                newMap.put(field, map.get(field));
            }
        }
        return newMap;
    }

    //参数替换
    public static String convert(String content, Map<String, Object> map) {
        if(StringUtils.isEmpty(content)){
            return null;
        }
        String newContent = replaceExpression(content);
        Map<String, Object> newMap = resetExpressionVar(content, map);
        for (Map.Entry entry : newMap.entrySet()) {
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(newContent);
            content = matcher.replaceAll(entry.getValue().toString());
        }
        return content;
    }
}
