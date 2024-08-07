package pro.wuan.privilege.vo;

import jakarta.validation.constraints.*;
import lombok.Data;


/**
 * 批量新增字段实体VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class BatchSaveListColumnVO {

    /**
     * json字符串。例如:[{"columnName":"字段名称1","columnCode":"字段注解1","remark":"备注1"},{"columnName":"字段名称2","columnCode":"字段注解2","remark":"备注2"}]
     */
    @NotBlank(message = "json不能为空")
    private String json;

    /**
     * 菜单资源id
     */
    @NotNull(message = "菜单资源id不能为空")
    private Long menuResourceId;

}
