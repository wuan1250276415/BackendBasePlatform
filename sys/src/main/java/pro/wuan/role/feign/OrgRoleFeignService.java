package pro.wuan.role.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;
import pro.wuan.role.mapper.OrgRoleMapper;
import pro.wuan.role.service.IOrgRoleService;

import java.util.List;

/**
 * 角色
 *
 * @author: liumin
 * @date: 2021-08-30 11:15:00
 */
@Slf4j
@RestController
@RequestMapping("/feign/org/role")
public class OrgRoleFeignService extends QueryFeignService<IOrgRoleService, OrgRoleMapper, OrgRoleModel> {

    @Autowired
    private IOrgRoleService roleService;

    /**
     * 根据组织机构id获取关联的角色列表
     *
     * @param organId 组织机构id
     * @return 角色列表
     */
    @GetMapping("/getRoleListByOrganId")
    public List<OrgRoleModel> getRoleListByOrganId(@RequestParam("organId") Long organId) {
        try {
            return roleService.getRoleListByUnitId(organId, null);
        } catch (Exception e) {
            log.error("role getRoleListByOrganId error:" + e);
            return null;
        }
    }

}
