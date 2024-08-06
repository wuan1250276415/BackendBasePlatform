package pro.wuan.common.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.constant.ConstantUser;
import pro.wuan.common.web.service.IColumnFilterService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 有权限查询的接口
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-05 14:55
 **/
public interface QueryAuthController<Service extends IBaseService, Mapper extends IBaseMapper, IdModel extends IDModel> extends IBaseController<Mapper, IdModel> {
    /**
     * 获取数据权限的Service
     *
     * @return
     */
    IColumnFilterService getColumnFilterService();

    /**
     * 根据查询条件及数据权限过滤返回所有符合查询结果的数据，依据列表数据权限过滤字段，并转换。
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体（数据权限过滤，列表权限过滤）", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPageByColumnWithDataFilter")
    @ResponseBody
    default Result<IPage<IdModel>> selectPageByColumnWithDataFilter(@RequestBody PageSearchParam pageSearchParam, HttpServletRequest request) {
        Result<IPage<IdModel>> result = getBaseService().selectPageByDataFilter(pageSearchParam, request, ConstantUser.getSystemParam());
        db2pageList(result.getResult().getRecords());
        columnFilter(result, request.getServletPath());
        return result;
    }

    default void columnFilter(Result<IPage<IdModel>> result, String url) {
        if (null == getColumnFilterService() || null == result.getResult().getRecords() || result.getResult().getRecords().size() == 0) {
            return;
        }
        List idModels = result.getResult().getRecords();
        Map<String, Object> columnAndNames = getColumnFilterService().columnFilter(url, idModels);
        result.getResult().setRecords(idModels);
        result.setRtnObj(columnAndNames);
    }
}
