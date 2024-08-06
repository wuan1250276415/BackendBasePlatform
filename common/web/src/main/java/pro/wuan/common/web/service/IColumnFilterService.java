package pro.wuan.common.web.service;

import jakarta.validation.constraints.NotNull;
import pro.wuan.common.core.model.IDModel;

import java.util.List;
import java.util.Map;

/**
 * 列显示权限接口
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-04 14:44
 **/
public interface IColumnFilterService {

    Map<String, Object> getListColumnListByCurrentUser(@NotNull String url, @NotNull List<? extends IDModel> idModels);

    Map<String, Object> columnFilter(@NotNull String url, @NotNull List<IDModel> idModels);

}
