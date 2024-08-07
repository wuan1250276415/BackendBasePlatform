package pro.wuan.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.EmptyController;
import pro.wuan.user.entity.OrgUserLoginStatusModel;
import pro.wuan.user.mapper.OrgUserLoginStatusMapper;
import pro.wuan.user.service.IOrgUserLoginStatusService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 在线用户
 * @program: tellhowcloud
 * @author huangtianji
 * @create 2022-10-11 09:35:17
 */
@RestController
@RequestMapping("/user/login/status")
public class OrgUserLoginStatusController extends EmptyController<IOrgUserLoginStatusService, OrgUserLoginStatusMapper, OrgUserLoginStatusModel> {

    /**
     * 在线用户-批量下线用户
     * @author huangtianji
     * @param userIds 逗号分割的id字符串
     * @return 失败或者成功的结果
     */
    @AspectLog(description = "在线用户-批量下线用户", type = OperationType.OPERATION_UPDATE)
    @GetMapping("/shutdown/batchIds")
    @ResponseBody
    public Result<String> shutdownBatchIds(String userIds) {
        Assert.notNull(userIds, "请传入参数！");
        List<Long> _ids = new ArrayList<>(Arrays.asList(userIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
        service.shutdownBatchIds(_ids);
        return Result.success();
    }

    /**
     * 在线用户-根据查询条件返回所有符合查询结果的数据，并转换。
     * @author huangtianji
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    public Result<IPage<OrgUserLoginStatusModel>> selectPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        //在线用户的信息更新
        service.updateUserStatus();
        //默认登录时间排序
        QueryWrapper<OrgUserLoginStatusModel> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().orderByDesc(OrgUserLoginStatusModel::getLoginStatus).orderByDesc(OrgUserLoginStatusModel::getLoginTime);
        pageSearchParam.setWrapper(queryWrapper);
        Result<IPage<OrgUserLoginStatusModel>> result = service.selectPage(pageSearchParam);
        db2pageList(result.getResult().getRecords());
        return result;
    }

}
