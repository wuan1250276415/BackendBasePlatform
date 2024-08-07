package pro.wuan.privilege.feign;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.feign.service.impl.QueryFeignService;
import pro.wuan.feignapi.userapi.entity.OrgRolePrivilegeModel;
import pro.wuan.privilege.mapper.OrgRolePrivilegeMapper;
import pro.wuan.privilege.service.IOrgRolePrivilegeService;

/**
 * 角色权限关联表
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@RestController
@RequestMapping("/feign/org/rolePrivilege")
public class OrgRolePrivilegeFeignService extends QueryFeignService<IOrgRolePrivilegeService, OrgRolePrivilegeMapper, OrgRolePrivilegeModel> {

}
