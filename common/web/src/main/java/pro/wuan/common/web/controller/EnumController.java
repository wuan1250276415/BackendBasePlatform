package pro.wuan.common.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.web.sysenum.SysEnumCache;

/**
 * 系统内的枚举查询，后端通知前端使用的key
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-16 14:21
 **/
@RestController
@Controller
@RequestMapping(value = "/enum")
@Slf4j
public class EnumController {

    @Autowired
    private SysEnumCache sysEnumCache;

    /**
     * 根据枚举类的key加载枚举类
     * 比如：userState
     *
     * @param enumKey 枚举类的key
     * @return 序列化后的枚举类
     */
    @GetMapping(value = "/select")
    public Result getSystemEmun(String enumKey) {
        Class clazz = sysEnumCache.getSysEnum(enumKey);
        if (null == clazz) {
            return Result.failure("找不到指定的系统枚举" + enumKey);
        }
        Object[] enums = clazz.getEnumConstants();
        return Result.success(enums);
    }
}
