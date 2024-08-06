package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import pro.wuan.feignapi.userapi.entity.OrgBusinessResourceModel;
import pro.wuan.feignapi.userapi.feign.OrgBusinessResourceFeignClient;
import pro.wuan.feignapi.userapi.vo.SearchMenuVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务资源
 *
 * @author: liumin
 * @date: 2021/11/19 10:07
 */
@Component
public class OrgBusinessResourceFeignClientFallback implements OrgBusinessResourceFeignClient {

    @Override
    public OrgBusinessResourceModel selectById(Long id) {
        return null;
    }

    @Override
    public List<OrgBusinessResourceModel> selectBatchByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    /**
     * 获取所有搜索菜单列表
     *
     * @return 搜索菜单列表
     */
    public List<SearchMenuVO> getAllSearchMenuList() {
        return new ArrayList<>();
    }

}
