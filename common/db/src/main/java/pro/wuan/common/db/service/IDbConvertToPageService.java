package pro.wuan.common.db.service;


import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseFeignQuerySerivce;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.SpringBeanUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author chenzhong
 * @description DB与Page的转换逻辑
 * @2019年5月10日
 */
public interface IDbConvertToPageService {

    public void execute(IDModel idModel);

    public void executeField(IDModel idModel, Field field);

    public void executeListToPage(List<? extends IDModel> idModels);

    default IBaseService getService(Class classes) {
        ServiceType serviceType = (ServiceType) classes.getAnnotation(ServiceType.class);
        return getService(serviceType);
    }

    default IBaseService getService(ServiceType serviceType) {
        String serverName = serviceType.serviceName();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(serverName)) {
            return (IBaseService) SpringBeanUtil.getBean(serverName);
        }
        return (IBaseService) SpringBeanUtil.getBean(serviceType.service());
    }

    default IBaseFeignQuerySerivce getBaseFeignQueryService(Class classes) {
        return (IBaseFeignQuerySerivce) SpringBeanUtil.getBean(classes);
    }
}
