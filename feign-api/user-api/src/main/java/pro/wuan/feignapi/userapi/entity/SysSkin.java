package pro.wuan.feignapi.userapi.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.common.core.model.BaseDeleteModel;

import jakarta.validation.constraints.*;

//系统皮肤,提供给用户选择
@TableName(value = "sys_skin")
@Getter
@Setter
public class SysSkin extends BaseDeleteModel {


    /**
     * 主皮肤  保存一个16色码，例如 #FFDEAD
     */
    @NotBlank(message = "主皮肤不能为空")
    private String mainSkin;


    /**
     * 子皮肤  保存一个16色码，例如 #FFDEAD
     */
    private String subSkin;



}
