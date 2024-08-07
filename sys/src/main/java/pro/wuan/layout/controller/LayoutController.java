package pro.wuan.layout.controller;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.userapi.entity.Layout;
import pro.wuan.layout.mapper.LayoutMapper;
import pro.wuan.layout.service.ILayoutService;

import java.util.List;

/**
 * 系统布局
 * @program: tellhowcloud
 * @author zf
 * @create 2021-08-30 11:15:00
 */
@RestController
@RequestMapping("/layout")
public class LayoutController extends CURDController<ILayoutService, LayoutMapper, Layout> {


    @Autowired
    private ILayoutService layoutService;




    /**
     * 获取所有系统布局列表
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Result getList() {
        try {
            List<Layout> list=layoutService.selectList(null);
            return Result.success().putData("list",list);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("获取数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }





    /**
     * 添加系统布局
     * @param layout
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@Validated(value = ValidatorSaveCheck.class) @RequestBody Layout layout) {
        try {
            return  layoutService.save(layout);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("添加数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }











}
