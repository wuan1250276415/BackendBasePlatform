package pro.wuan.common.db.convertor.db2page;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.annotation.ListToList;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.BaseSubModel;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.constant.CascadConstant;
import pro.wuan.common.db.service.IDbConvertToPageService;
import pro.wuan.common.db.util.TableUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ivan
 * @description 将DB中的list转化为page中的list，执行二次查询
 * 适用场景：将list转换为前端的明细表内容
 */
@Slf4j
@Service
public class DbListConvertToPageList implements IDbConvertToPageService {

    @Override
    public void execute(IDModel idModel) {
        if (idModel == null) {
            return;
        }
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToList.class);
        for (Field field : fields) {
            this.executeField(idModel, field);
        }
    }

    @Override
    public void executeField(IDModel idModel, Field field) {
        if (idModel == null || field == null) {
            return;
        }
        if (field.isAnnotationPresent(ListToList.class)) {
            log.debug("开始模块{}.{}的属性转换，将Db-List转换成Page-List", idModel.getClass().getName(), field.getName());
            ListToList listToList = field.getAnnotation(ListToList.class);
            String fk = listToList.fk();
            Class classes = listToList.fkType();

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

            ServiceType serviceType = listToList.fkType().getAnnotation(ServiceType.class);
            if (serviceType != null) {
                IBaseService coreService = getService(serviceType);

                List<? extends IDModel> rtnList = coreService.selectList(queryWrapper);
                // 添加数据转换时候子类型为basesubmodel的属性强制设置为modify
                if( null != rtnList && rtnList.size() > 0){
                    if(rtnList.get(0) instanceof BaseSubModel){
                        rtnList.stream().forEach(obj -> {
                            ReflectionUtils.setFieldValue(obj, "state", CascadConstant.MODIFY_TYPE);
                        });
                    }
                }
                ReflectionUtils.setFieldValue(idModel, field.getName(), rtnList);
                log.debug("模块{}.{}的属性转换正常，将{}值转换为{}",
                        idModel.getClass().getName(),
                        field.getName(),
                        field.getName(),
                        rtnList);
            } else {
                log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解", idModel.getClass().getName(), field.getName(), idModel.getClass().getName());
            }
        }
    }

    @Override
    public void executeListToPage(List<? extends IDModel> idModels) {
        //获取第1个Entity 作为class
        IDModel idModel = idModels.get(0);
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToList.class);
        List<Object> ids = new ArrayList<Object>();
        for (IDModel model : idModels) {
            ids.add(model.getId());
        }
        for (Field field : fields) {
            if (!idModel.hasConvert(field)) {
                continue;
            }
            if (field.isAnnotationPresent(ListToList.class)) {
                log.debug("开始模块{}.{}的属性转换，将Db-List转换成Page-List", idModel.getClass().getName(), field.getName());
                ListToList listToList = field.getAnnotation(ListToList.class);

                String fk = listToList.fk();
                Class classes = listToList.fkType();

                QueryWrapper queryWrapper = Wrappers.query();
                queryWrapper.in(TableUtil.property2column(fk, classes), ids);

                try {
                    Field sortField = classes.getField("sort");
                    if (sortField != null) {
                        queryWrapper.orderByAsc("sort");
                    }
                } catch (Exception e) {
                    log.warn("{}将Db-List转换成Page-List的过程找不到sort字段，信息如下:{}", classes.getName(), e.getMessage());
                }

                ServiceType serviceType = listToList.fkType().getAnnotation(ServiceType.class);
                if (serviceType != null) {
                    IBaseService coreService = getService(serviceType);
                    List<? extends IDModel> rtnList = coreService.selectList(queryWrapper);

                    // 添加数据转换时候子类型为basesubmodel的属性强制设置为modify
                    if( null != rtnList && rtnList.size() > 0){
                        if(rtnList.get(0) instanceof BaseSubModel){
                            rtnList.stream().forEach(obj -> {
                                ReflectionUtils.setFieldValue(obj, "state", CascadConstant.MODIFY_TYPE);
                            });
                        }
                    }

                    for (IDModel model : idModels) {
                        List<IDModel> valList = new ArrayList<>();
                        for (IDModel subModel : rtnList) {
                            Object fieldValue = ReflectionUtils.getFieldValue(subModel, fk);
                            if (null != fieldValue && Long.compare((Long) fieldValue, model.getId()) == 0) {
                                valList.add(subModel);
                            }
                        }
                        ReflectionUtils.setFieldValue(model, field.getName(), valList);
                    }

                    log.debug("模块{}.{}的属性转换正常，将{}值转换为{}",
                            idModel.getClass().getName(),
                            field.getName(),
                            field.getName(),
                            rtnList);
                } else {
                    log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解", idModel.getClass().getName(), field.getName(), idModel.getClass().getName());
                }
            }
        }
    }

}
