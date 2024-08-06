package pro.wuan.common.core.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 分页查询内容
 * @author ivan
 * @blame
 * @since 2020/7/16
 */
@Data
public class PageSearchParam extends BaseSearchParam{

    /**
     * 默认每页条数
     */
    @NotNull
    private int limit = 10;

    /**
     * 默认起始页
     */
    @NotNull
    private int page = 1;

}
