package pro.wuan.post.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorQueryCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.constant.SearchOptConstant;
import pro.wuan.common.core.model.BaseQueryValue;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.feignapi.userapi.entity.OrgPostModel;
import pro.wuan.feignapi.userapi.entity.OrgUserModel;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.feignapi.userapi.model.UserDetails;
import pro.wuan.post.mapper.OrgPostMapper;
import pro.wuan.post.service.IOrgPostService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021-08-30 11:19:43
 */
@RestController
@RequestMapping("/org/post")
public class OrgPostController extends CURDController<IOrgPostService, OrgPostMapper, OrgPostModel> {

    /**
     * 分页查询实体
     *
     * @param pageSearchParam
     * @return
     */
    @AspectLog(description = "分页查询实体", type = OperationType.OPERATION_QUERY)
    @PostMapping(value = "/selectPage")
    @ResponseBody
    @Override
    public Result<IPage<OrgPostModel>> selectPage(@RequestBody @Validated(value = ValidatorQueryCheck.class) PageSearchParam pageSearchParam) {
        String key = "deptId";
        if (pageSearchParam.peek(key) == null) {
            UserDetails userDetails = UserContext.getCurrentUser();
            OrgUserModel user = userDetails.getUser();
            if (user == null) {
                return Result.failure("当前用户信息为空");
            }
            if (CommonConstant.USER_TYPE.SUPER_ADMIN.getValue().equals(user.getType())) { // 超级管理员
                return service.selectPage(pageSearchParam);
            } else {
                BaseQueryValue queryValue = new BaseQueryValue();
                queryValue.setOpt(SearchOptConstant.SEARCH_EQUAL);
                OrgOrganModel unit = userDetails.getUnit();
                if (unit != null) {
                    queryValue.setValues(new Object[]{unit.getId()});
                } else {
                    queryValue.setValues(new Object[]{CommonConstant.DEFAULT_ID});
                }
                pageSearchParam.addQueryParams(key, queryValue);
            }
        }
        return service.selectPage(pageSearchParam);
    }

    /**
     * 保存岗位
     *
     * @param postModel 岗位实体
     * @return 岗位实体
     */
    @AspectLog(description = "保存岗位", type = OperationType.OPERATION_ADD)
    @PostMapping("/savePost")
    public Result<OrgPostModel> savePost(@RequestBody @Validated(value = ValidatorSaveCheck.class) OrgPostModel postModel) {
        try {
            return service.savePost(postModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 更新岗位
     *
     * @param postModel 岗位实体
     * @return 岗位实体
     */
    @AspectLog(description = "更新岗位", type = OperationType.OPERATION_UPDATE)
    @PostMapping("/updatePost")
    public Result<OrgPostModel> updatePost(@RequestBody @Validated(value = ValidatorUpdateCheck.class) OrgPostModel postModel) {
        try {
            return service.updatePost(postModel);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 删除岗位
     *
     * @param id 岗位id
     * @return 删除成功/失败
     */
    @AspectLog(description = "删除岗位", type = OperationType.OPERATION_DELETE)
    @GetMapping("/deletePost")
    public Result<String> deletePost(@Validated @NotNull(message = "id不能为空") Long id) {
        try {
            return service.deletePost(id);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 获取组织机构下的岗位列表
     *
     * @param organId 组织机构id
     * @return 岗位实体列表
     */
    @AspectLog(description = "获取组织机构下的岗位列表", type = OperationType.OPERATION_QUERY)
    @GetMapping("/getPostListByOrgan")
    public Result<List<OrgPostModel>> getPostListByOrgan(@Validated @NotNull(message = "organId不能为空") Long organId) {
        try {
            return service.getPostListByOrgan(organId);
        } catch (Exception e) {
            return Result.failure(ExceptionUtil.getMessage(e));
        }
    }

}
