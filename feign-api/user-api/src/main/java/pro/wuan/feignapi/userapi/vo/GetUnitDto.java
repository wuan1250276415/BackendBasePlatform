package pro.wuan.feignapi.userapi.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;

import java.io.Serializable;
import java.util.List;

@Data
public class GetUnitDto implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 本级单位id不能为空
     */
    @NotBlank(message = "题目内容不能为空",groups = {ValidatorSaveCheck.class})
    private Long unitId;

    /**
     * 单位类型
     */
    @NotNull
    private List<String> organTypes;

}
