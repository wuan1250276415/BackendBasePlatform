package pro.wuan.common.web.service.impl;


import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.web.service.IColumnFilterService;
import pro.wuan.feignapi.userapi.entity.OrgListColumnModel;
import pro.wuan.feignapi.userapi.feign.OrgListColumnFeignClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 列显示权限接口
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-04 15:18
 **/
public class ColumnFilterServiceImpl implements IColumnFilterService {

    private OrgListColumnFeignClient orgListColumnFeignClient;

    public ColumnFilterServiceImpl(OrgListColumnFeignClient orgListColumnFeignClient) {
        this.orgListColumnFeignClient = orgListColumnFeignClient;
    }

    @Override
    public Map<String, Object> getListColumnListByCurrentUser(String url, List<? extends IDModel> idModels) {
        List<OrgListColumnModel> orgListColumnModels = orgListColumnFeignClient.findListColumnByUrl(url);
        //当key值重复时，后者覆盖前者
        Map<String, Object> columnCodeAndName = orgListColumnModels.stream().collect(Collectors.toMap(OrgListColumnModel::getColumnCode, OrgListColumnModel::getColumnName, (v1, v2) -> v2));
        // 默认显示所有数据
//        for (IDModel idModel : idModels) {
//            Field[] fields = ReflectionUtils.getDeclaredFields(idModel.getClass());
//            for (Field field : fields) {
//                if (!Modifier.isFinal(field.getModifiers()) && !columnCodeAndName.containsKey(field.getName())) {
//                    ReflectionUtils.setFieldValue(idModel, field.getName(), null);
//
//                }
//            }
//        }
        return columnCodeAndName;
    }

    @Override
    public Map<String, Object> columnFilter(String url, List<IDModel> idModels) {
        Map<String, Object> columnCodeAndName = this.getListColumnListByCurrentUser(url, idModels);
        return columnCodeAndName;
    }
}
