package pro.wuan.common.db.convertor.db2page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pro.wuan.common.core.annotation.IDToObject;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.service.IDbConvertToPageService;
import pro.wuan.common.db.util.TableUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DbIdConvertToPageObject implements IDbConvertToPageService {

    @Override
    public void execute(IDModel idModel) {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), IDToObject.class);
        for (Field field : fields) {
            this.executeField(idModel, field);
        }
    }

    @Override
    public void executeField(IDModel idModel, Field field) {
        if (idModel == null || field == null || !idModel.hasConvert(field)) {
            return;
        }
        if (field.isAnnotationPresent(IDToObject.class)) {
            log.debug("开始模块{}.{}的属性转换，将Id转换成Object", idModel.getClass().getName(), field.getName());
            IDToObject idToObject = field.getDeclaredAnnotation(IDToObject.class);
            Object fieldValue = ReflectionUtils.getFieldValue(idModel, field.getName());
            String targetProperty = idToObject.targetProperty();

            if (!StringUtils.isEmpty(fieldValue)) {
                IBaseFeignQuerySerivce baseFeignQuerySerivce = null;
                IBaseService subCoreService = null;
                //表示微服务调用
                if (idToObject.feignService().length > 0) {
                    Class<? extends IBaseFeignQuerySerivce> serviceClass = idToObject.feignService()[0];
                    baseFeignQuerySerivce = getBaseFeignQueryService(serviceClass);

                } else { // 表示调用本地的数据库查询服务
                    ServiceType subServiceType = idToObject.type().getAnnotation(ServiceType.class);
                    if (subServiceType != null) {
                        subCoreService = getService(subServiceType);
                    }
                }

                if (null == subCoreService && null == baseFeignQuerySerivce) {
                    log.warn("模块{}.{}的type实体对应的serviceType注解或者feignService至少存在一个", idModel.getClass().getName(), field.getName());
                    return;
                }

                IDModel subModel = null;
                String uniqueKey = idToObject.uniqueKey();

                if (null != baseFeignQuerySerivce) {
                    subModel = baseFeignQuerySerivce.selectById(Long.parseLong(fieldValue.toString()));
                } else if (null != subCoreService) {
                    if ("id".equalsIgnoreCase(uniqueKey)) {
                        subModel = subCoreService.selectById(Long.parseLong(fieldValue.toString()));
                    } else {
                        QueryWrapper queryWrapper = new QueryWrapper();
                        queryWrapper.setEntityClass(idToObject.type());
                        queryWrapper.eq(TableUtil.property2column(uniqueKey, idToObject.type()), fieldValue);
                        subModel = subCoreService.selectOne(queryWrapper);
                    }

                }

                if (null != subModel) {
                    ReflectionUtils.setFieldValue(idModel, targetProperty, subModel);
                    log.debug("模块{}.{}的属性转换正常，转换至{}",
                            idModel.getClass().getName(),
                            field.getName(),
                            targetProperty
                    );
                } else {
                    log.error("模块{}.{}的属性转换异常，无法获取类{}对应唯一属性{}为{}的数据",
                            idModel.getClass().getName(),
                            field.getName(),
                            idModel.getClass().getName(),
                            uniqueKey,
                            fieldValue);
                }

            } else {
                log.info("模块{}.{}的属性值不存在", idModel.getClass().getName(), field.getName());
            }
        }
        log.debug("结束模块{}.{}的属性转换结束", idModel.getClass().getName());
    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        //获取第1个Entity 作为class
        IDModel idModel = idModels.get(0);
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), IDToObject.class);
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            //获取idModels的filed字段的value赋值给listValue;
            List<Object> ids = new ArrayList<>();
            IDToObject idToObject = field.getDeclaredAnnotation(IDToObject.class);
            String targetProperty = idToObject.targetProperty();

            for (IDModel model : idModels) {
                Object fieldValue = ReflectionUtils.getFieldValue(model, field.getName());
                if (null != fieldValue) {
                    ids.add(fieldValue);
                }
            }

            IBaseFeignQuerySerivce baseFeignQuerySerivce = null;
            IBaseService subCoreService = null;
            //表示微服务调用
            if (idToObject.feignService().length > 0) {
                Class<? extends IBaseFeignQuerySerivce> serviceClass = idToObject.feignService()[0];
                baseFeignQuerySerivce = getBaseFeignQueryService(serviceClass);

            } else { // 表示数据库调用
                ServiceType subServiceType = idToObject.type().getAnnotation(ServiceType.class);
                if (subServiceType != null) {
                    subCoreService = getService(subServiceType);
                }
            }

            if (null == subCoreService && null == baseFeignQuerySerivce) {
                log.warn("模块{}.{}的type实体对应的serviceType注解或者feignService至少存在一个", idModel.getClass().getName(), field.getName());
                return;
            }

            List<IDModel> subModels = new ArrayList<>();
            String uniqueKey = idToObject.uniqueKey();

            if (null != baseFeignQuerySerivce) {
                subModels = baseFeignQuerySerivce.selectBatchByIds(ids);
            } else if (null != subCoreService) {
                if ("id".equalsIgnoreCase(uniqueKey)) {
                    subModels = subCoreService.selectBatchIds(ids);
                } else {
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.setEntityClass(idToObject.type());
                    queryWrapper.in(TableUtil.property2column(uniqueKey, idToObject.type()), ids);
                    subModels = subCoreService.selectList(queryWrapper);
                }

            }

            Map<String, IDModel> map = new Hashtable<String, IDModel>();
            for (IDModel subModel : subModels) {
                Object setValues = ReflectionUtils.getFieldValue(subModel, uniqueKey);
                map.put(setValues.toString(), subModel);
            }

            //替换原idModels中的值
            for (IDModel model : idModels) {
                Object fieldValue = ReflectionUtils.getFieldValue(model, field.getName());
                if (null != fieldValue) {
                    IDModel idModel1 = map.get(String.valueOf(fieldValue));
                    if (null == idModel1) {
                        log.debug("模块{}.{}的属性转换失败，{}值无法找到对应的数据",
                                idModel.getClass().getName(),
                                field.getName(),
                                fieldValue);
                        continue;
                    }
                    ReflectionUtils.setFieldValue(model, targetProperty, idModel1);
                    log.debug("模块{}.{}的属性转换正常，将{}值转换为{}",
                            idModel.getClass().getName(),
                            field.getName(),
                            uniqueKey,
                            targetProperty
                    );
                }
            }
        }
    }
}
