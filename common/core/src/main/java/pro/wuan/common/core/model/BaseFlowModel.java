package pro.wuan.common.core.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseFlowModel extends BaseTenantModel {

    private String title;//待办事件的名称

    private String status;//当前审批状态

    @TableField(exist = false)
    private String sourceType;// 数据来源 page

    //审批按钮标识
    @TableField(exist = false)
    private Boolean approveButton;
}
