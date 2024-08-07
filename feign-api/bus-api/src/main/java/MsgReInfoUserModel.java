import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;
import pro.wuan.common.core.model.IDModel;

import java.util.Date;

/**
 * 消息类型实体<br>
 *
 * @author lucky
 * @version 1.0, 2021-09-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("msg_re_info_user")
public class MsgReInfoUserModel extends IDModel {

    private static final long serialVersionUID = 1L;
    /**
     * 业务key
     */
    private String key;
    //columns START
    /**
     * 消息id
     */
    private Long msgInfoId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 是否阅读,1:已读，2:未读 ,99全部已读
     */
    private Integer readFlag;
    /**
     * 删除标记，1:正常,2:已删除
     */
    private Integer deleteFlag;
    /**
     * 阅读时间
     */
    private Date readTime;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 应用id
     */
    private Long appId;
    /**
     * 消息分类id
     */
    private Long typeId;

    /**
     * 消息主题
     */
    @TableField(exist = false)
    private String title;
    /**
     * 消息内容
     */
    @TableField(exist = false)
    private String content;
    /**
     * 消息分类图标
     */
    @TableField(exist = false)
    private String icon;
    /**
     * 消息样式
     */
    @TableField(exist = false)
    private String style;

    @TableField(exist = false)
    private String typeName;
    /**
     * 消息ids
     */
    @TableField(exist = false)
    private String msgIds;

    /**
     * 通知方式
     */
    @TableField(exist = false)
    private String noticeType;

    /**
     * 定时发送时间,如果为空立即发送
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @TableField(exist = false)
    private Date timing;
    /**
     * 应用编码
     */
    @TableField(exist = false)
    private String appCode;
    /**
     * 消息编码
     */
    @TableField(exist = false)
    private String typeCode;

    /**
     * 详情url
     */
    @TableField(exist = false)
    private String detailUrl;
    //columns END
}
