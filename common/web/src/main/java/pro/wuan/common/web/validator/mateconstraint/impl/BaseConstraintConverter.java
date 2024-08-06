package pro.wuan.common.web.validator.mateconstraint.impl;

import cn.hutool.core.map.MapUtil;
import org.apache.commons.lang3.StringUtils;
import pro.wuan.common.web.validator.mateconstraint.IConstraintConverter;
import pro.wuan.common.web.validator.model.ConstraintInfo;
import pro.wuan.common.web.validator.utils.ValidatorConstants;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 约束提取基础类
 *
 */
public abstract class BaseConstraintConverter implements IConstraintConverter {

    private final List<String> methods = Collections.emptyList();

    /**
     * 支持的类型
     *
     * @param clazz 类型
     * @return 是否支持
     */
    @Override
    public boolean support(Class<? extends Annotation> clazz) {
        if (getSupport().isEmpty()) {
            return true;
        }
        return clazz != null && getSupport().contains(clazz);
    }

    /**
     * 转换
     *
     * @param ano 注解
     * @return 约束信息
     * @throws Exception 异常信息
     */
    @Override
    public ConstraintInfo converter(Annotation ano) throws Exception {
        Class<? extends Annotation> clazz = ano.getClass();
        Map<String, Object> attr = MapUtil.newHashMap();
        for (String method : getMethods()) {
            Object value = clazz.getMethod(method).invoke(ano);
            attr.put(method, value);
        }
        this.convert(attr);
        return new ConstraintInfo().setType(getType(ano.annotationType())).setAttrs(attr);
    }

    /**
     * 替换数据校验map里面的message的占位符，比如{max},{min}
     * @param map 数据校验map
     */
    private void convert(Map<String, Object> map) {
        if(map.containsKey(ValidatorConstants.MESSAGE)){
            String message = map.get(ValidatorConstants.MESSAGE).toString();
            if(!StringUtils.isEmpty(message)){
                for (Map.Entry entry : map.entrySet()) {
                    String regex = "\\{" + entry.getKey() + "\\}";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(message);
                    message = matcher.replaceAll(entry.getValue().toString());
                }
                map.put(ValidatorConstants.MESSAGE,message);
            }
        }
    }

    /**
     * 子类返回各自具体支持的验证注解 类型
     *
     * @return 注解
     */
    protected abstract List<Class<? extends Annotation>> getSupport();

    /**
     * 子类返回需要反射的验证注解的 字段值
     *
     * @return 方法
     */
    protected List<String> getMethods() {
        return methods;
    }

    /**
     * 子类返回自定义的类型
     *
     * @param type 注解类型
     * @return
     */
    protected abstract String getType(Class<? extends Annotation> type);

}
