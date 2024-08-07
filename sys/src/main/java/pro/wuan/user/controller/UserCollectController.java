package pro.wuan.user.controller;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.UserCollect;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.user.mapper.UserCollectMapper;
import pro.wuan.user.service.IUserCollectService;

import java.util.List;


/**
 * 用户收藏
 * @program: tellhowcloud
 * @author zf
 * @create 2021-08-30 11:15:00
 */
@RestController
@RequestMapping("/userCollect")
public class UserCollectController extends CURDController<IUserCollectService, UserCollectMapper, UserCollect> {


    @Autowired
    private IUserCollectService userCollectService;


    /**
     * 获取当前用户所有收藏
     * @return
     */
    //@AspectLog(description = "获取当前用户所有收藏", type = OperationType.OPERATION_QUERY)
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Result getList() {
        try {
            QueryWrapper<UserCollect> queryWrapper=new QueryWrapper();
            queryWrapper.eq("create_user_id", UserContext.getCurrentUserId());
            queryWrapper.orderByAsc("sort");
            List<UserCollect> list=userCollectService.selectList(queryWrapper);
            return Result.success().putData("list",list);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("获取数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }



    /**
     *新增（更新）用户收藏
     * @param collects 封装多个用户收藏列表
     * @return
     */
    //@AspectLog(description = "新增（更新）用户收藏", type = OperationType.OPERATION_UPDATE)
    @RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
    public Result batchUpdate(@Validated  @RequestBody List<UserCollect> collects) {
        try {
            return userCollectService.batchUpdate(collects);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("修改数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }



}
