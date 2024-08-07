import feign.ParamFeignClient;
import model.SysParamModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典接口降级服务
 *
 * @author: oldone
 * @date: 2021/9/8 10:59
 */
@Component
public class ParamFeignClientFallback implements ParamFeignClient {

    /**
     * 向sys注册系统参数
     *
     * @param sysParamModels      系统参数类名
     * @param sysParamModels 系统参数对象列表
     */
    @Override
    public Boolean registParam(List<SysParamModel> sysParamModels) {
        return true;
    }

    /**
     * 根据应用的paramClass全类名码获取这个类的所有属性参数
     *
     * @param paramClass 注册的参数类
     * @return 属性与值的map
     */
    @Override
    public Map<String, Object> getSysParam(String paramClass) {
        return new HashMap<String, Object>();
    }
}
