package pro.wuan.param.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import model.SysParamModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.log.AspectLog;
import pro.wuan.common.log.constant.OperationType;
import pro.wuan.common.web.controller.EmptyController;
import pro.wuan.param.mapper.SysParamMapper;
import pro.wuan.param.service.ISysParamService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 系统参数
 *
 * @author hawkwang
 * @program: tellhowcloud
 * @create 2021-09-06 17:20:20
 */
@RestController
@RequestMapping("/param")
@Validated(value = ValidatorUpdateCheck.class)
public class SysParamController extends EmptyController<ISysParamService, SysParamMapper, SysParamModel> {

    /**
     * 系统参数-编辑-读取系统中所有的SysParamModel
     *
     * @return 所有带参数的
     */
    @AspectLog(description = "获取所有的系统参数")
    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public Result<List<SysParamModel>> getSysParamModel() {
        LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(SysParamModel::getParamClass);
        List<SysParamModel> list = service.selectList(lambdaQueryWrapper);
        super.db2pageList(list);
        return Result.success(list);
    }

    /**
     * 系统参数-保存-批量保存系统中所有的参数配置
     *
     * @param sysParamModels
     * @return 成功/失败
     */
    @AspectLog(description = "更新所有的系统参数", type = OperationType.OPERATION_UPDATE)
    @RequestMapping(path = "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> updateSysParam(@RequestBody @Valid List<SysParamModel> sysParamModels) {
        if (service.batchUpdateNoCascade(sysParamModels)) {
            service.reloadSysParam();
            return Result.success("succsss");
        } else {
            return Result.failure("falure");
        }
    }

    /**
     * 全局-获取-前端根据唯一标识获取对应系统参数值
     *
     * @param unique 系统参数唯一标识
     * @return 成功/失败
     */
    @AspectLog(description = "获取单一的系统参数")
    @RequestMapping(path = "/selectOne", method = RequestMethod.GET)
    public Result selectSysParamByUnique(String unique) {
        String className = StringUtils.substringBeforeLast(unique, ".");
        String propertyName = StringUtils.substringAfterLast(unique,".");
        Map<String,Object> obj = service.selectSysParamByParamClass(className);
        Object val = "";
        for(String key : obj.keySet()){
            if(key.equalsIgnoreCase(propertyName)){
                val = obj.get(key);
                break;
            }
        }
        return Result.success(val);
    }
}
