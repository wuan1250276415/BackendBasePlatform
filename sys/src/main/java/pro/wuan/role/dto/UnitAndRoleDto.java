package pro.wuan.role.dto;

import lombok.Data;

/**
 * 单位角色接值
 */
@Data
public class UnitAndRoleDto {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 组织机构id
     */
    private Long organId;

    /**
     * 保存状态
     */
    private String saveStatus = "Y";

    /**
     * 单位ID串
     */
    private String unitTreeIds;

}
