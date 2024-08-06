package pro.wuan.feignapi.userapi.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetAllUnitAndDeptsDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 单位类型
     */
    @NotNull
    private List<String> organTypes;

}
