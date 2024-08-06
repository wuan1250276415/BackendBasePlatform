package pro.wuan.common.db.convertor.page2db;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pro.wuan.common.core.annotation.ArrayToList;
import pro.wuan.common.core.annotation.IdsToList;
import pro.wuan.common.core.annotation.ListToList;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.BaseSubModel;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.IDUtil;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.core.utils.SpringBeanUtil;
import pro.wuan.common.db.constant.CascadConstant;
import pro.wuan.common.db.service.IDispatcherService;
import pro.wuan.common.db.util.TableUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Page2DbConvertDispatcher implements IDispatcherService {

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void dispatcherSave(IDModel idModel) {
        if(null == idModel){
            return;
        }
        try {
            //原有注解处理
            idsToList(idModel);
            arrayToList(idModel);
            //新注解处理
            pageListToDbList(idModel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void dispatcherUpdate(IDModel idModel) throws RuntimeException {
        if(null == idModel){
            return;
        }
        try {
            this.dispatcherSave(idModel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * @param idModel
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @description 页面传递的数组转换为model中的list
     */
    public void arrayToList(IDModel idModel) throws InstantiationException, IllegalAccessException {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ArrayToList.class);
        for (Field field : fields) {
            ArrayToList arrayToList = field.getDeclaredAnnotation(ArrayToList.class);
            Object fieldValue =  ReflectionUtils.getFieldValue(idModel, field.getName());
            String convertProperty = arrayToList.convertProperty();
            String targetProperty = arrayToList.targetProperty();
            Class clazz = arrayToList.type();
            if(null != fieldValue){
                List<IDModel> setValues = new ArrayList<>();
                if(fieldValue instanceof List){
                    List list = (List) fieldValue;
                    for(Object x : list){
                        IDModel eachObj = (IDModel) clazz.newInstance();
                        ReflectionUtils.setFieldValue(eachObj, convertProperty, x);
                        setValues.add(eachObj);
                    }
                }
                ReflectionUtils.setFieldValue(idModel, targetProperty, setValues);
            }
        }
    }

    /**
     * @param idModel
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @description 页面传递的ids转换为model中的list
     */
    public void idsToList(IDModel idModel) throws InstantiationException, IllegalAccessException {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), IdsToList.class);
        for (Field field : fields) {
            IdsToList idsToList = field.getDeclaredAnnotation(IdsToList.class);
            String fieldValue = (String) ReflectionUtils.getFieldValue(idModel, field.getName());
            String convertProperty = idsToList.convertProperty();
            String targetProperty = idsToList.targetProperty();
            Class clazz = idsToList.type();
            if (!StringUtils.isEmpty(fieldValue)) {
                String[] eachFields = fieldValue.split(";");
                List<IDModel> setValues = new ArrayList<>();
                for (String eachField : eachFields) {
                    IDModel eachObj = (IDModel) clazz.newInstance();
                    ReflectionUtils.setFieldValue(eachObj, convertProperty, Long.valueOf(eachField));
                    setValues.add(eachObj);
                }
                ReflectionUtils.setFieldValue(idModel, targetProperty, setValues);
            }
        }
    }

    /**
     * @param idModel
     * @description 将页面封装成的List对象新增插入到DB中
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void pageListToDbList(IDModel idModel) {
        List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToList.class);
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                ListToList relation = (ListToList) field.getAnnotation(ListToList.class);
                //获取明细表数据清单
                List<? extends IDModel> subList = (List<? extends IDModel>) ReflectionUtils.getFieldValue(idModel, field.getName());
                if (subList != null && !subList.isEmpty()) {
                    boolean isFirst = true;
                    //批处理场景下使用
                    List<IDModel> saveList = new ArrayList<>();
                    for (IDModel subModel : subList) {
                        //明细表（页面添加明细表详细内容）
                        if (subModel instanceof BaseSubModel) {
                            BaseSubModel baseSubModel = (BaseSubModel) subModel;
                            //对页面新增的进行数据库插入操作
                            if (CascadConstant.ADD_TYPE.equals(baseSubModel.getState())) {
                                ReflectionUtils.setFieldValue(baseSubModel, relation.fk(), idModel.getId());
                                baseSubModel.setId(null);
                                getService(baseSubModel.getClass()).insert(baseSubModel);
                            }
                            //对页面删除的进行数据库删除操作
                            else if (CascadConstant.DELETE_TYPE.equals(baseSubModel.getState())) {
                                getService(baseSubModel.getClass()).deleteById(baseSubModel.getId());
                            }
                            //对页面更新的进行数据库更新操作
                            else if (CascadConstant.MODIFY_TYPE.equals(baseSubModel.getState())) {
                                getService(baseSubModel.getClass()).updateById(baseSubModel);
                            }
                            //默认逻辑不处理（基准行不处理）
                        }
                        //外键关系表（如人员选择、部门选择等）
                        else {
                            //删除原有数据
                            if (isFirst) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put(TableUtil.property2column(relation.fk(), subModel.getClass()), idModel.getId());
                                getService(subModel.getClass()).deleteByMap(map);
                                isFirst = false;
                            }
                            ReflectionUtils.setFieldValue(subModel, relation.fk(), idModel.getId());
                            ReflectionUtils.setFieldValue(subModel, "id", IDUtil.nextId());
                            saveList.add(subModel);
                            //getService(subModel.getClass()).save(subModel);
                        }
                    }
                    //如果存在批处理场景
                    if (saveList != null && !saveList.isEmpty()) {
                        IDModel subModel = saveList.get(0);
                        getService(subModel.getClass()).batchInsertNoCascade(saveList);
                    }
                }
                //明细表清单为空的情况下，执行删除操作，将原有数据全部删除
                else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(TableUtil.property2column(relation.fk(), relation.fkType()), idModel.getId());
                    getService(relation.fkType()).deleteByMap(map);
                }

            }
        }
    }

    /**
     * 获取主从表mapper
     *
     * @param classes
     * @return
     */
    public IBaseService getService(Class classes) {
        ServiceType serviceType = (ServiceType) classes.getAnnotation(ServiceType.class);
        String serverName = serviceType.serviceName();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(serverName)) {
            return (IBaseService) SpringBeanUtil.getBean(serverName);
        }
        return (IBaseService) SpringBeanUtil.getBean(serviceType.service());
    }

}
