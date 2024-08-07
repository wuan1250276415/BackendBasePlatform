package pro.wuan.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import model.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import pro.wuan.common.core.annotation.param.PropertyInteger;
import pro.wuan.common.core.annotation.param.PropertyRadio;
import pro.wuan.common.core.annotation.param.PropertyString;
import pro.wuan.common.core.annotation.param.SystemProperty;
import pro.wuan.param.service.ISysParamService;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 系统启动完毕时需要运行的程序
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-08 14:55
 **/
@Slf4j
@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value(value = "${spring.application.name}")
    private String applicationName;

    @Autowired
    private ISysParamService sysParamService;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initSystemParam();
        Map<String, Object> systemProperties = event.getApplicationContext().getBeansWithAnnotation(SystemProperty.class);
        for (String key : systemProperties.keySet()) {
            Object systemProperty = systemProperties.get(key);
            Class systemPropertyBean = systemProperty.getClass();
            String className = systemPropertyBean.getName();
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
                    saveOrUpdateParam(model);
                } else if (field.isAnnotationPresent(PropertyInteger.class)) { //数字类型
                    PropertyInteger propertyInteger = field.getAnnotation(PropertyInteger.class);
                    String name = propertyInteger.name();
                    int defaultValue = propertyInteger.defaultValue();
                    model.setParamType(PropertyInteger.class.getSimpleName());
                    model.setParamDefault(String.valueOf(defaultValue));
                    model.setParamValue(model.getParamDefault());
                    model.setParamName(name);
                    saveOrUpdateParam(model);
                } else if (field.isAnnotationPresent(PropertyRadio.class)) { //radio类型
                    PropertyRadio propertyRadio = field.getAnnotation(PropertyRadio.class);
                    String name = propertyRadio.name();
                    String defaultValue = propertyRadio.defaultValue();
                    model.setParamSelect(JSONObject.toJSONString(propertyRadio.options()));
                    model.setParamType(PropertyRadio.class.getSimpleName());
                    model.setParamDefault(defaultValue);
                    model.setParamValue(model.getParamDefault());
                    model.setParamName(name);
                    saveOrUpdateParam(model);
                } else {
                    continue;
                }

            }
        }
        afterSystemParamUpdated();
    }

    /**
     * 对系统参数进行全部维护前先把当前应用的系统参数都临时设置成0
     */
    private void initSystemParam() {
        LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysParamModel::getAppCode, applicationName);

        SysParamModel sysParamModel = new SysParamModel();
        sysParamModel.setStatus(1);
        sysParamService.update(sysParamModel, lambdaQueryWrapper);
    }

    /**
     * 删除所有的临时状态为1的参数，加载到内存里面
     */
    private void afterSystemParamUpdated() {
        LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysParamModel::getAppCode, applicationName).eq(SysParamModel::getStatus, 1);
        sysParamService.delete(lambdaQueryWrapper);
        sysParamService.saveSysParamInRedis(applicationName);

    }

    /**
     * 更新或者新增系统参数
     *
     * @param model 系统参数对象
     */
    private void saveOrUpdateParam(SysParamModel model) {
        String paramUnique = model.getParamUnique();
        Assert.notNull(paramUnique, "系统参数的唯一标识不能为空");
        LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysParamModel::getParamUnique, paramUnique);
        SysParamModel sysParamModel = sysParamService.selectOne(lambdaQueryWrapper);
        //如果为空，就插入数据
        if (null == sysParamModel) {
            sysParamService.insert(model);
        } else {
            //更新
            sysParamModel.setParamClass(model.getParamClass());
            sysParamModel.setParamName(model.getParamName());
            sysParamModel.setParamCode(model.getParamCode());
            sysParamModel.setParamSelect(model.getParamSelect());
            sysParamModel.setParamDefault(model.getParamDefault());

            sysParamModel.setParamOptions(model.getParamOptions());
            sysParamModel.setParamType(model.getParamType());
            sysParamModel.setAppCode(model.getAppCode());
            sysParamModel.setStatus(0);
            sysParamService.updateAllById(sysParamModel);
        }
    }
}
