package pro.wuan.common.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDeleteModel extends BaseModel {

    /**
     * @ignore
     * 删除标识
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @TableLogic(value = "false",delval = "true")
    private boolean deleteFlag;
}
