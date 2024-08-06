package pro.wuan.feignapi.userapi.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import pro.wuan.common.core.model.BaseDeleteModel;

import jakarta.validation.constraints.*;

//用户收藏
@TableName(value = "org_user_collect")
@Data
public class UserCollect extends BaseDeleteModel {


    /**
     *排序号
     */
    @NotNull
    @Min(value=1,message = "排序号必须大于等于1")
    private Integer sort;


    /**
     * 菜单名称
     */
    @NotNull
    private String menuName;


    /**
     * 菜单url
     */
    @NotNull
    private String menuUrl;


    /**
     * 收藏名称
     */
    @NotNull
    private String collectName;



}
