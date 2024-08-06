package pro.wuan.common.web.validator.mateconstraint.impl;


import pro.wuan.common.core.annotation.constraints.annotation.Unique;
import pro.wuan.common.web.validator.mateconstraint.IConstraintConverter;
import pro.wuan.common.web.validator.utils.ValidatorConstants;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 实体属性唯一校验
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-17 21:15
 **/
public class UniqueConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {
    /**
     * 子类返回各自具体支持的验证注解 类型
     *
     * @return 注解
     */
    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return List.of(Unique.class);
    }

    /**
     * 子类返回自定义的类型
     *
     * @param type 注解类型
     * @return 类型名
     */
    @Override
    protected String getType(Class<? extends Annotation> type) {
        return type.getSimpleName();
    }

    /**
     * 转换到前端的属性
     *
     * @return 属性列表
     */
    @Override
    protected List<String> getMethods() {
        return Arrays.asList("url", "params", ValidatorConstants.MESSAGE);
    }

}
