package pro.wuan.skin.controller;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.controller.CURDController;
import pro.wuan.feignapi.ossapi.fegin.SysAttMainService;
import pro.wuan.feignapi.userapi.entity.SysSkin;
import pro.wuan.skin.mapper.SysSkinMapper;
import pro.wuan.skin.service.ISysSkinService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统皮肤
 * @program: tellhowcloud
 * @author zf
 * @create 2021-08-30 11:15:00
 */
@RestController
@RequestMapping("/sysSkin")
public class SysSkinController extends CURDController<ISysSkinService, SysSkinMapper, SysSkin> {


    @Autowired
    private ISysSkinService sysSkinService;
    @Resource
    @Lazy
    private SysAttMainService attMainService;



    /**
     * 获取所有系统皮肤列表
     * @return
     */
    //@AspectLog(description = "获取所有系统皮肤列表", type = OperationType.OPERATION_QUERY)
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public Result getList() {
        try {
            List<SysSkin> list=sysSkinService.selectList(null);
            return Result.success().putData("list",list);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("获取数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }




    /**
     * 添加系统皮肤
     * @param sysSkin 封装系统皮肤
     * @return
     */
    //@AspectLog(description = "添加系统皮肤", type = OperationType.OPERATION_ADD)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@Validated @RequestBody SysSkin sysSkin) {
        try {
            return  sysSkinService.save(sysSkin);
        } catch (Exception e) {
            return Result.failure(StrUtil.format("添加数据失败，异常如下：{}", ExceptionUtil.getMessage(e)));
        }
    }





}
