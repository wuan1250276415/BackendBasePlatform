package pro.wuan.common.core.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @author: posiding
 * @create:2019-04-16 16:56
 **/
@Getter
@Setter
public class BaseSubModel extends IDModel {

    /**
     * 子表页面状态 add delete
     */
    @TableField(exist = false)
    public String state;

    /**
     * 子表记录排序
     */
    public Integer sort;

}
