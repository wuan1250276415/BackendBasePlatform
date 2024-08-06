package pro.wuan.feignapi.userapi.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pro.wuan.common.core.model.BaseDeleteModel;
import jakarta.validation.constraints.*;

//用户自定义设置
@TableName(value = "org_user_set")
@Data
public class UserSet  extends BaseDeleteModel {


    /**
     * 用户选择的皮肤id
     */
    @NotNull(message = "皮肤id不能为空")
    private Long skinId;


    /**
     * 封装皮肤对象
     * @ignore
     */
    @TableField(exist = false)
    private SysSkin sysSkin;


    /**
     * 用户选择的布局id
     */
    @NotNull(message = "布局id不能为空")
    private Long layoutId;


    /**
     * 封装布局对象
     * @ignore
     */
    @TableField(exist = false)
    private Layout layout;


}
