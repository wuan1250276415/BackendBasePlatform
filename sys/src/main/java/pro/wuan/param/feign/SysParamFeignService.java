package pro.wuan.param.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import model.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pro.wuan.param.service.ISysParamService;

import java.util.List;
import java.util.Map;


/**
 * 系统参数
 *
 * @author hawkWang
 * @program: tellhowcloud
 * @create 2021-09-06 17:20:20
 */
@RestController
@RequestMapping(value = "/feign/param")
public class SysParamFeignService {

    @Autowired
    private ISysParamService sysParamService;

    /**
     * 向sys注册系统参数
     *
     * @param sysParamModels 参数列表
     * @param sysParamModels 系统参数对象列表
     */
    @PostMapping(value = "/registParam", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Boolean registParam(@RequestBody List<SysParamModel> sysParamModels) {
        if (null == sysParamModels && sysParamModels.size() == 0) {
            return false;
        }
        String appCode = sysParamModels.get(0).getAppCode();
        //注册之前将该类的属性都置为无效
        {
            SysParamModel sysParamModel = new SysParamModel();
            sysParamModel.setStatus(1);
            LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysParamModel::getAppCode, appCode);
            sysParamService.update(sysParamModel, lambdaQueryWrapper);
        }

        //依据属性更新数据
        for (SysParamModel model : sysParamModels) {
            LambdaQueryWrapper<SysParamModel> hasQuery = new LambdaQueryWrapper<>();
            hasQuery.eq(SysParamModel::getParamUnique, model.getParamUnique());
            SysParamModel sysParamModel = sysParamService.selectOne(hasQuery);
            if (null == sysParamModel) {
                sysParamService.insert(model);
            } else {
                //更新
                sysParamModel.setParamClass(model.getParamClass());
                sysParamModel.setParamName(model.getParamName());
                sysParamModel.setParamCode(model.getParamCode());
                sysParamModel.setParamSelect(model.getParamSelect());
                sysParamModel.setParamDefault(model.getParamDefault());

                sysParamModel.setParamOptions(model.getParamOptions());
                sysParamModel.setParamType(model.getParamType());
                sysParamModel.setParamValue(model.getParamDefault());
                sysParamModel.setAppCode(model.getAppCode());
                sysParamModel.setStatus(0);
                sysParamService.updateAllById(sysParamModel);
            }
        }
        //更新之后删除多余的
        {
            LambdaQueryWrapper<SysParamModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysParamModel::getParamClass, appCode).eq(SysParamModel::getStatus, 1);
            sysParamService.delete(lambdaQueryWrapper);
        }

        sysParamService.saveSysParamInRedis(appCode);
        return true;
    }

    /**
     * 根据应用的paramClass全类名码获取这个类的所有属性参数
     *
     * @param paramClass 注册的参数类
     * @return 属性与值的map
     */
    @GetMapping("/selectByParamClass")
    public Map<String, Object> getSysParam(@RequestParam("paramClass") String paramClass) {
        return sysParamService.selectSysParamByParamClass(paramClass);
    }
}
