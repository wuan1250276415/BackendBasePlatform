package pro.wuan.common.core.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseTenantModel extends BaseDeleteModel {

    /**
     * @ignore
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;//租户id
}
