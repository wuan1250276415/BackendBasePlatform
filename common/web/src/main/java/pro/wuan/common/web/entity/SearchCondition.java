package pro.wuan.common.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.model.BaseDeleteModel;


/**
 * 搜索条件实体类
 *
 * @author: oldone
 * @date: 2021/9/3 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("search_condition")
public class SearchCondition extends BaseDeleteModel {
    /**
     * 资源码
     */
    @NotNull(message = "资源码不能为空", groups = {ValidatorSaveCheck.class})
    private String sourceCode;
    /**
     * 搜索参数--JSON格式
     */
    @NotNull(message = "搜索参数不能为空", groups = {ValidatorSaveCheck.class})
    private String searchParams;
    /**
     * 搜索条件名称
     */
    @NotNull(message = "搜索条件不能为空", groups = {ValidatorSaveCheck.class})
    //@Unique(message = "搜索名称已存在", url = "/search/aa", service = SearchConditionServiceImpl.class,  groups = {ValidatorSaveCheck.class})
    private String searchName;
    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = {ValidatorSaveCheck.class})
    private Integer sort;
}
