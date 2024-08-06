package pro.wuan.feignapi.dictapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.BaseTenantModel;
import pro.wuan.feignapi.dictapi.constant.DictSaveCheck;

import java.util.List;

/**
 * 数据字典实体类
 *
 * @author: oldone
 * @date: 2021/9/7 14:41
 */
@Data
@TableName("sys_dictionary")
public class SysDictionary extends BaseTenantModel {
    /**
     * 字典编码
     */
    // @NotNull(message = "字典编码不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Length(max = 100, message = "字典编码长度不超过100个字符", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String code;
    /**
     * 字典名称
     */
    @NotNull(message = "字典名称不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, DictSaveCheck.class})
    @Length(max = 200, message = "字典编码长度不超过100个字符", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String name;
    /**
     * 字典值
     */
    @NotNull(message = "字典值不能为空", groups = {DictSaveCheck.class})
    @Length(max = 200, message = "字典编码长度不超过100个字符", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private String value;
    /**
     * 所属应用
     */
    @NotNull(message = "所属应用不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, DictSaveCheck.class})
    private String appId;
    /**
     * 父节点id
     */
    @NotNull(message = "上级字典不能为空", groups = {DictSaveCheck.class})
    private Long parentId;
    /**
     * 字典类型id
     */
    @NotNull(message = "上级字典类型不能为空", groups = {DictSaveCheck.class})
    private Long typeId;
    /**
     * 排序
     */
    @Range(min = 0, max = 9999, message = "排序值范围0-9999", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    private Integer sort;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否显示
     */
    private boolean displayFlag;
    /**
     * 是否树形字典
     */
    private boolean treeFlag;
    /**
     * 是否字典目录
     */
    private boolean catalogueFlag;

    /**
     * 字典项
     */
    @TableField(exist = false)
    private List<SysDictionary> dictItems;
    /**
     * 是否有子项
     */
    @TableField(exist = false)
    private Boolean hasChildren;
}
