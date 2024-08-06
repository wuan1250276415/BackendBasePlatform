package pro.wuan.feignapi.userapi.fallback;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.feignapi.userapi.feign.OrgRoleFeignClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021/11/19 10:07
 */
@Component
public class OrgRoleFeignClientFallback implements OrgRoleFeignClient {

    @Override
    public OrgRoleModel selectById(Long id) {
        return null;
    }

    @Override
    public List<OrgRoleModel> selectBatchByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    /**
     * 根据组织机构id获取关联的角色列表
     *
     * @param organId 组织机构id
     * @return 角色列表
     */
    public List<OrgRoleModel> getRoleListByOrganId(@RequestParam("organId") Long organId) {
        return new ArrayList<>();
    }

}
