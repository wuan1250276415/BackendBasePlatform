package pro.wuan.common.web.validator.extract;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import pro.wuan.common.web.validator.constant.ValidatorConstant;
import pro.wuan.common.web.validator.mateconstraint.IConstraintConverter;
import pro.wuan.common.web.validator.mateconstraint.impl.*;
import pro.wuan.common.web.validator.model.ConstraintInfo;
import pro.wuan.common.web.validator.model.FieldValidatorDesc;
import pro.wuan.common.web.validator.model.ValidConstraint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static pro.wuan.common.web.validator.constant.ValidatorConstant.*;


/**
 * 缺省的约束提取器
 *
 */
@Slf4j
public class DefaultConstraintExtractImpl implements IConstraintExtract {

    private final Map<String, Map<String, FieldValidatorDesc>> CACHE = new HashMap<>();

    private final Validator validator;
    private BeanMetaDataManager beanMetaDataManager;
    private List<IConstraintConverter> constraintConverters;

    public DefaultConstraintExtractImpl(final Validator validator) {
        this.validator = validator;
        init();
    }

    public final void init() {
        try {
            Field beanMetaDataManagerField = ValidatorImpl.class.getDeclaredField("beanMetaDataManager");
            beanMetaDataManagerField.setAccessible(true);
            beanMetaDataManager = (BeanMetaDataManager) beanMetaDataManagerField.get(validator);
            constraintConverters = new ArrayList<>(10);
            constraintConverters.add(new MaxMinConstraintConverter());
            constraintConverters.add(new NotNullConstraintConverter());
            constraintConverters.add(new RangeConstraintConverter());
            constraintConverters.add(new RegExConstraintConverter());
            constraintConverters.add(new UniqueConstraintConverter());
            //所有的转换数据格式写在上方
            constraintConverters.add(new OtherConstraintConverter());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("初始化验证器失败", e);
        }
    }

    @Override
    public Collection<FieldValidatorDesc> extract(List<ValidConstraint> constraints) throws Exception {
        if (constraints == null || constraints.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, FieldValidatorDesc> fieldValidatorDesc = new HashMap((int) (constraints.size() / 0.75 + 1));
        for (ValidConstraint constraint : constraints) {
            doExtract(constraint, fieldValidatorDesc);
        }

        return fieldValidatorDesc.values();
    }


    private void doExtract(ValidConstraint constraint, Map<String, FieldValidatorDesc> fieldValidatorDesc) throws Exception {
        Class<?> targetClazz = constraint.getTarget();
        Class<?>[] groups = constraint.getGroups();

        String key = targetClazz.getName() + ValidatorConstant.COLON +
                Arrays.stream(groups).map(Class::getName).collect(Collectors.joining(ValidatorConstant.COLON));
        if (CACHE.containsKey(key)) {
            fieldValidatorDesc.putAll(CACHE.get(key));
            return;
        }

        //测试一下这个方法
        //validator.getConstraintsForClass(targetClazz).getConstrainedProperties()

        BeanMetaData<?> res = beanMetaDataManager.getBeanMetaData(targetClazz);
        Set<MetaConstraint<?>> r = res.getMetaConstraints();
        for (MetaConstraint<?> metaConstraint : r) {
            builderFieldValidatorDesc(metaConstraint, groups, fieldValidatorDesc);
        }

        CACHE.put(key, fieldValidatorDesc);
    }


    private void builderFieldValidatorDesc(MetaConstraint<?> metaConstraint, Class<?>[] groups,
                                           Map<String, FieldValidatorDesc> fieldValidatorDesc) throws Exception {
        //字段上的组
        Set<Class<?>> groupsMeta = metaConstraint.getGroupList();
        boolean isContainsGroup = false;

        //需要验证的组
        for (Class<?> group : groups) {
            if (groupsMeta.contains(group)) {
                isContainsGroup = true;
                break;
            }
            for (Class<?> g : groupsMeta) {
                if (g.isAssignableFrom(group)) {
                    isContainsGroup = true;
                    break;
                }
            }
        }
        if (!isContainsGroup) {
            return;
        }

        ConstraintLocation con = metaConstraint.getLocation();
        //TODO 目前不支持在class上面的数据校验注解
        if (null == con.getConstrainable()) {
            return;
        }
        String domainName = con.getDeclaringClass().getSimpleName();
        String fieldName = con.getConstrainable().getName();
        String key = domainName + fieldName;

        FieldValidatorDesc desc = fieldValidatorDesc.get(key);
        if (desc == null) {
            desc = new FieldValidatorDesc();
            desc.setField(fieldName);
            desc.setFieldType(getType(con.getConstrainable().getType().getTypeName()));
            desc.setConstraints(new ArrayList<>());
            fieldValidatorDesc.put(key, desc);
        }
        ConstraintInfo constraint = builderConstraint(metaConstraint.getDescriptor().getAnnotation());
        desc.getConstraints().add(constraint);

        //添加子表
        if(con.getConstrainable().getDeclaringClass().getDeclaredField(fieldName).isAnnotationPresent(Valid.class)){
            Map<String, FieldValidatorDesc> subFieldValidatorDesc = new HashMap<>();
            if(ARRAY.equals(desc.getFieldType())){
                String clazz = StringUtils.substringBetween(con.getConstrainable().getType().getTypeName(), "<", ">");
                try {
                    Class<?> subTargetClazz = ClassUtils.getClass(clazz);
                    BeanMetaData<?> subRes = beanMetaDataManager.getBeanMetaData(subTargetClazz);
                    Set<MetaConstraint<?>> subR = subRes.getMetaConstraints();
                    for (MetaConstraint<?> subMetaConstraint : subR) {
                        builderFieldValidatorDesc(subMetaConstraint, groups, subFieldValidatorDesc);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("未找到Class{}", clazz);
                }
            }
            if(subFieldValidatorDesc.size() == 0){
                log.warn("统一数据权限，不支持当前格式{}，忽略数据校验", desc.getFieldType());
            }
            desc.setChildren(new ArrayList<>(subFieldValidatorDesc.values()));
        }
//        if (PATTERN.equals(metaConstraint.getDescriptor().getAnnotationType().getSimpleName())) {
//            ConstraintInfo notNull = new ConstraintInfo();
//            notNull.setType(NOT_NULL);
//            Map<String, Object> attrs = MapUtil.newHashMap();
//            attrs.put(MESSAGE, "不能为空");
//            notNull.setAttrs(attrs);
//            desc.getConstraints().add(notNull);
//        }
    }


    private String getType(String typeName) {
        if (StrUtil.startWithAny(typeName, SET_TYPE_NAME, LIST_TYPE_NAME, COLLECTION_TYPE_NAME)) {
            return ARRAY;
        } else if (StrUtil.equalsAny(typeName, LONG_TYPE_NAME, INTEGER_TYPE_NAME, SHORT_TYPE_NAME)) {
            return INTEGER;
        } else if (StrUtil.equalsAny(typeName, DOUBLE_TYPE_NAME, FLOAT_TYPE_NAME)) {
            return FLOAT;
        } else if (StrUtil.equalsAny(typeName, LOCAL_DATE_TIME_TYPE_NAME, DATE_TYPE_NAME)) {
            return DATETIME;
        } else if (StrUtil.equalsAny(typeName, LOCAL_DATE_TYPE_NAME)) {
            return DATE;
        } else if (StrUtil.equalsAny(typeName, LOCAL_TIME_TYPE_NAME)) {
            return TIME;
        } else if (StrUtil.equalsAny(typeName, BOOLEAN_TYPE_NAME)) {
            return BOOLEAN;
        }
        return StrUtil.subAfter(typeName, CharUtil.DOT, true);
    }

    private ConstraintInfo builderConstraint(Annotation annotation) throws Exception {
        for (IConstraintConverter constraintConverter : constraintConverters) {
            if (constraintConverter.support(annotation.annotationType())) {
                return constraintConverter.converter(annotation);
            }
        }
        return null;
    }
}
