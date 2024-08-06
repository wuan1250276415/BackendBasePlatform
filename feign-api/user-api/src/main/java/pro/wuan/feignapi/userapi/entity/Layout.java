package pro.wuan.feignapi.userapi.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.BaseDeleteModel;


//系统布局,提供给用户选择
@TableName(value = "sys_layout")
@Getter
@Setter
public class Layout extends BaseDeleteModel {


    /**
     * 布局key,唯一，前端根据key渲染布局
     */
    @NotBlank(message = "布局key不能为空" , groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String layoutKey;


    /**
     * 布局名称
     */
    @NotNull(message = "布局名称不能为空", groups = {ValidatorSaveCheck.class,ValidatorUpdateCheck.class})
    private String layoutName;


}
