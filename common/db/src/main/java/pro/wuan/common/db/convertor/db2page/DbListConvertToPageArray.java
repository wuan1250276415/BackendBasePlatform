package pro.wuan.common.db.convertor.db2page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.annotation.ListToArray;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.service.IDbConvertToPageService;
import pro.wuan.common.db.util.TableUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: tellhowcloud
 * @create: 2021-08-26 14:06
 **/
@Service
@Slf4j
public class DbListConvertToPageArray implements IDbConvertToPageService {

    @Override
    public void execute(IDModel idModel) {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToArray.class);
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            log.debug("开始模块{}.{}的属性转换，将List转换成targetProperty", idModel.getClass().getName(), field.getName());
            //获取field的值
            executeField(idModel, field);
        }
    }

    @Override
    public void executeField(IDModel idModel, Field field) {
        try {
            if (idModel == null || field == null)
                return;
            if (field.isAnnotationPresent(ListToArray.class)) {
                List list = new ArrayList();
                ListToArray listToArray = field.getAnnotation(ListToArray.class);
                String fk = listToArray.fk();
                Class classes = listToArray.fkType();
                String convertProperty =  listToArray.convertProperty();
                String targetProperty = listToArray.targetProperty();
                ServiceType serviceType = listToArray.fkType().getAnnotation(ServiceType.class);
                QueryWrapper queryWrapper = Wrappers.query();
                queryWrapper.eq(TableUtil.property2column(fk, classes), idModel.getId());
                try {
                    Field sortField = classes.getField("sort");
                    if (sortField != null) {
                        queryWrapper.orderByAsc("sort");
                    }
                } catch (Exception e) {
                    log.warn("{}将Db-List转换成Page-List的过程找不到sort字段，信息如下:{}", classes.getName(), e.getMessage());
                }
                if (serviceType != null) {
                    IBaseService coreService = getService(serviceType);
                    List<? extends IDModel> rtnList = coreService.selectList(queryWrapper);
                    for(int i = 0; i < rtnList.size(); i ++){
                        IDModel fieldObject = (IDModel) rtnList.get(i);
                        list.add(ReflectionUtils.getFieldValue(fieldObject,convertProperty));
                    }
                } else {
                    log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解", idModel.getClass().getName(), field.getName(), idModel.getClass().getName());
                }
                ReflectionUtils.setFieldValue(idModel, targetProperty, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ListToArray的过程中出现异常，信息如下：", e.getMessage());
        }

    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        //获取第1个Entity 作为class
        IDModel idModel = idModels.get(0);
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToArray.class);
        List<Object> ids = new ArrayList<Object>();
        for (IDModel model : idModels) {
            ids.add(model.getId());
        }
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            if (field.isAnnotationPresent(ListToArray.class)) {
                List list = new ArrayList();
                ListToArray listToArray = field.getAnnotation(ListToArray.class);
                String fk = listToArray.fk();
                Class classes = listToArray.fkType();
                String convertProperty =  listToArray.convertProperty();
                String targetProperty = listToArray.targetProperty();
                ServiceType serviceType = listToArray.fkType().getAnnotation(ServiceType.class);
                QueryWrapper queryWrapper = Wrappers.query();
                queryWrapper.eq(TableUtil.property2column(fk, classes), ids);
                try {
                    Field sortField = classes.getField("sort");
                    if (sortField != null) {
                        queryWrapper.orderByAsc("sort");
                    }
                } catch (Exception e) {
                    log.warn("{}将Db-List转换成Page-List的过程找不到sort字段，信息如下:{}", classes.getName(), e.getMessage());
                }
                if (serviceType != null) {
                    IBaseService coreService = getService(serviceType);
                    List<? extends IDModel> rtnList = coreService.selectList(queryWrapper);
                    for(int i = 0; i < rtnList.size(); i ++){
                        IDModel fieldObject = (IDModel) rtnList.get(i);
                        list.add(ReflectionUtils.getFieldValue(fieldObject,convertProperty));
                    }
                } else {
                    log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解", idModel.getClass().getName(), field.getName(), idModel.getClass().getName());
                }
                ReflectionUtils.setFieldValue(idModel, targetProperty, list);
            }
        }
    }

}
