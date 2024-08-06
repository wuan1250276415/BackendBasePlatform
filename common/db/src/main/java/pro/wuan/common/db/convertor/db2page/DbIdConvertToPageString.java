package pro.wuan.common.db.convertor.db2page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pro.wuan.common.core.annotation.ConvertMapping;
import pro.wuan.common.core.annotation.IDToString;
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
public class DbIdConvertToPageString implements IDbConvertToPageService {

    @Override
    public void execute(IDModel idModel) {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), IDToString.class);
        for (Field field : fields) {
            this.executeField(idModel, field);
        }
    }

    @Override
    public void executeField(IDModel idModel, Field field) {
        if (idModel == null || field == null) {
            return;
        }
        if (field.isAnnotationPresent(IDToString.class)) {
            log.debug("开始模块{}.{}的属性转换，将Id转换成String", idModel.getClass().getName(), field.getName());
            IDToString idToString = field.getDeclaredAnnotation(IDToString.class);
            Object fieldValue = ReflectionUtils.getFieldValue(idModel, field.getName());
            if (!StringUtils.isEmpty(fieldValue)) {
                IBaseFeignQuerySerivce baseFeignQuerySerivce = null;
                IBaseService subCoreService = null;
                //表示微服务调用
                if (idToString.feignService().length > 0) {
                    Class<? extends IBaseFeignQuerySerivce> serviceClass = idToString.feignService()[0];
                    baseFeignQuerySerivce = getBaseFeignQueryService(serviceClass);

                } else {
                    ServiceType subServiceType = idToString.type().getAnnotation(ServiceType.class);
                    if (subServiceType != null) {
                        subCoreService = getService(subServiceType);
                    }
                }
                if (baseFeignQuerySerivce != null || subCoreService != null) {
                    ConvertMapping[] convertMappings = idToString.mappings();
                    for (ConvertMapping convertMapping : convertMappings) {
                        IDModel subModel = null;
                        String uniqueKey = idToString.uniqueKey();
                        if (StringUtils.isEmpty(uniqueKey)) {
                            if (baseFeignQuerySerivce == null) {
                                subModel = subCoreService.selectById(Long.parseLong(fieldValue.toString()));
                            } else {
                                subModel = baseFeignQuerySerivce.selectById(Long.parseLong(fieldValue.toString()));
                            }
                        } else {
                            QueryWrapper queryWrapper = new QueryWrapper();
                            queryWrapper.setEntityClass(idToString.type());
                            queryWrapper.eq(TableUtil.property2column(uniqueKey, idToString.type()), fieldValue);
                            subModel = subCoreService.selectOne(queryWrapper);
                        }

                        if (subModel != null) {
                            Object setValues = ReflectionUtils.getFieldValue(subModel, convertMapping.sourceProperty());
                            ReflectionUtils.setFieldValue(idModel, convertMapping.targetProperty(), setValues);
                            log.debug("模块{}.{}的属性转换正常，将{}值转换为{}",
                                    idModel.getClass().getName(),
                                    field.getName(),
                                    convertMapping.targetProperty(),
                                    setValues);
                        } else {
                            log.error("模块{}.{}的属性转换异常，无法获取类{}对应主键为{}的数据",
                                    idModel.getClass().getName(),
                                    field.getName(),
                                    idModel.getClass().getName(),
                                    idToString.type().getClass().getName(),
                                    fieldValue);
                        }
                    }
                } else {
                    log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解", idModel.getClass().getName(), field.getName(), idModel.getClass().getName());
                }
            }
            log.debug("结束模块{}.{}的属性转换，将List转换成String", idModel.getClass().getName(), field.getName());
        }
    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        //获取第1个Entity 作为class
        IDModel idModel = idModels.get(0);
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), IDToString.class);
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            //获取idModels的filed字段的value赋值给listValue;
            List<Object> ids = new ArrayList<>();
            IDToString idToString = field.getDeclaredAnnotation(IDToString.class);
            ConvertMapping[] convertMappings = idToString.mappings();
            if (field.isAnnotationPresent(IDToString.class)) {
                for (IDModel model : idModels) {
                    Object fieldValue = ReflectionUtils.getFieldValue(model, field.getName());
                    ids.add(fieldValue);
                }
            }

            IBaseFeignQuerySerivce baseFeignQuerySerivce = null;
            IBaseService subCoreService = null;
            //表示微服务调用
            if (idToString.feignService().length > 0) {
                Class<? extends IBaseFeignQuerySerivce> serviceClass = idToString.feignService()[0];
                baseFeignQuerySerivce = getBaseFeignQueryService(serviceClass);

            } else {
                ServiceType subServiceType = idToString.type().getAnnotation(ServiceType.class);
                if (subServiceType != null) {
                    subCoreService = getService(subServiceType);
                }
            }
            if (baseFeignQuerySerivce != null || subCoreService != null) {
                List<IDModel> subModels = null;
                String uniqueKey = "id";
                if (!StringUtils.isEmpty(idToString.uniqueKey())) {
                    uniqueKey = idToString.uniqueKey();
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.setEntityClass(idToString.type());
                    queryWrapper.in(TableUtil.property2column(uniqueKey, idToString.type()), ids);
                    subModels = subCoreService.selectList(queryWrapper);
                } else {
                    if (baseFeignQuerySerivce == null) {
                        subModels = subCoreService.selectBatchIds(ids);
                    } else {
                        try {
                            subModels = baseFeignQuerySerivce.selectBatchByIds(ids);
                        } catch (Exception e) {
                            log.info("no service");
                        }
                    }
                }

                Map<String, IDModel> map = new Hashtable<String, IDModel>();
                if (null != subModels) {
                    for (IDModel subModel : subModels) {
                        Object setValues = ReflectionUtils.getFieldValue(subModel, uniqueKey);
                        map.put(setValues.toString(), subModel);
                    }
                }
                //替换原idModels中的值
                for (IDModel model : idModels) {
                    Object fieldValue = ReflectionUtils.getFieldValue(model, field.getName());
                    if (null != fieldValue) {
                        IDModel idModel1 = map.get(fieldValue.toString());
                        if (null == idModel1) {
                            log.debug("模块{}.{}的属性转换失败，{}值无法找到对应的数据",
                                    idModel.getClass().getName(),
                                    field.getName(),
                                    fieldValue);
                            continue;
                        }
                        for (ConvertMapping convertMapping : convertMappings) {
                            Object setValue = ReflectionUtils.getFieldValue(idModel1, convertMapping.sourceProperty());
                            ReflectionUtils.setFieldValue(model, convertMapping.targetProperty(), setValue);
                            log.debug("模块{}.{}的属性转换正常，将{}值转换为{}",
                                    idModel.getClass().getName(),
                                    field.getName(),
                                    convertMapping.targetProperty(),
                                    fieldValue);
                        }
                    }
                }
            }
        }
    }
}
