package starter;

import com.alibaba.fastjson.JSONObject;
import feign.ParamFeignClient;
import model.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import pro.wuan.common.core.annotation.param.PropertyInteger;
import pro.wuan.common.core.annotation.param.PropertyRadio;
import pro.wuan.common.core.annotation.param.PropertyString;
import pro.wuan.common.core.annotation.param.SystemProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 外部服务监听当前应用系统参数
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-13 14:10
 **/
public class SysParamListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value(value = "${spring.application.name}")
    private String applicationName;

    @Autowired
    private ParamFeignClient paramFeignClient;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, Object> systemProperties = event.getApplicationContext().getBeansWithAnnotation(SystemProperty.class);
        for (String key : systemProperties.keySet()) {
            Object systemProperty = systemProperties.get(key);
            Class systemPropertyBean = systemProperty.getClass();
            String className = systemPropertyBean.getName();
            List<SysParamModel> sysParamModelList = new ArrayList<>();
            Field[] fields = systemPropertyBean.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                String uniqueName = className + "." + fieldName;
                SysParamModel model = new SysParamModel();
                model.setStatus(0);
                model.setAppCode(applicationName);
                model.setParamCode(fieldName);
                model.setParamUnique(uniqueName);
                model.setParamClass(className);

                if (field.isAnnotationPresent(PropertyString.class)) { //字符串类型
                    PropertyString propertyString = field.getAnnotation(PropertyString.class);
                    String name = propertyString.name();
                    String defaultValue = propertyString.defaultValue();
                    model.setParamType(PropertyString.class.getSimpleName());
                    model.setParamDefault(defaultValue);
                    model.setParamValue(defaultValue);
                    model.setParamName(name);
                    sysParamModelList.add(model);
                } else if (field.isAnnotationPresent(PropertyInteger.class)) { //数字类型
                    PropertyInteger propertyInteger = field.getAnnotation(PropertyInteger.class);
                    String name = propertyInteger.name();
                    int defaultValue = propertyInteger.defaultValue();
                    model.setParamType(PropertyInteger.class.getSimpleName());
                    model.setParamDefault(String.valueOf(defaultValue));
                    model.setParamValue(model.getParamDefault());
                    model.setParamName(name);
                    sysParamModelList.add(model);
                } else if (field.isAnnotationPresent(PropertyRadio.class)) { //radio类型
                    PropertyRadio propertyRadio = field.getAnnotation(PropertyRadio.class);
                    String name = propertyRadio.name();
                    String defaultValue = propertyRadio.defaultValue();
                    model.setParamSelect(JSONObject.toJSONString(propertyRadio.options()));
                    model.setParamType(PropertyRadio.class.getSimpleName());
                    model.setParamDefault(defaultValue);
                    model.setParamValue(model.getParamDefault());
                    model.setParamName(name);
                    sysParamModelList.add(model);
                } else {
                    continue;
                }

            }
            if (sysParamModelList.size() > 0) {
                paramFeignClient.registParam(sysParamModelList);
            }
        }

    }
}
