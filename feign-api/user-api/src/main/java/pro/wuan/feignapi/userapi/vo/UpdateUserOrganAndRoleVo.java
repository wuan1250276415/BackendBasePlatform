package pro.wuan.feignapi.userapi.vo;

import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 更新用户所属的组织机构信息及关联的角色vo
 *
 * @author: liumin
 * @date: 2022/4/19 16:35
 */
@Data
public class UpdateUserOrganAndRoleVo {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 组织机构id
     */
    @NotNull(message = "组织机构id不能为空")
    private Long organId;

}
