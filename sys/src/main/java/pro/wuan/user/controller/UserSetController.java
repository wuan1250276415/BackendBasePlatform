package pro.wuan.user.controller;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.UserSet;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.user.mapper.UserSetMapper;
import pro.wuan.user.service.IUserSetService;

/**
 * 用户设置
 * @program: tellhowcloud
 * @author zf
 * @create 2021-08-30 11:15:00
 */
@RestController
@RequestMapping("/userSet")
public class UserSetController extends CURDController<IUserSetService, UserSetMapper, UserSet> {


    @Autowired
    private IUserSetService userSetService;



    /**
     * 添加用户设置
     * @param userSet 封装用户设置信息
     * @return
     */
    //@AspectLog(description = "添加用户设置", type = OperationType.OPERATION_ADD)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@Validated  @RequestBody UserSet userSet) {
        try {
            return  userSetService.save(userSet);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("添加数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }




    /**
     * 根据用户id获取用户设置
     * @return
     */
    //@AspectLog(description = "根据用户id获取用户设置", type = OperationType.OPERATION_QUERY)
    @RequestMapping(value = "/getByUserId", method = RequestMethod.GET)
    public Result getByUserId() {
        try {
            UserSet  userSet=userSetService.getByUserId(UserContext.getCurrentUserId());
            return Result.success(userSet);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("获取数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }





}
