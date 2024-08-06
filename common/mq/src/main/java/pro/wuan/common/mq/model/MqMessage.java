package pro.wuan.common.mq.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.mq.constant.GroupEnum;
import pro.wuan.common.mq.constant.SzjyTagEnum;
import pro.wuan.common.mq.constant.TagEnum;

import java.util.Date;

/**
 * 待发送mq消息体
 *
 * @author: oldone
 * @date: 2021/9/17 17:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mq_message")
public class MqMessage extends IDModel {
    /**
     * 消息关键字，便宜查找--可以是业务主键id
     */
    private String key;
    /**
     * 思政消息确认回复关键字，便宜查找--可以是业务主键id
     */
    @TableField(exist = false)
    private String replyId;
    /**
     * 思政消息确认回复内容
     */
    @TableField(exist = false)
    private String replyContent;
    /**
     * 消息接受人id
     */
    private String toId;
    /**
     * 消息主题
     */
    private String title;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 消息分类
     */
    private String type;
    /**
     * 通知方式
     */
    private String noticeType;
    /**
     * 消息详细信息链接
     */
    private String detailUrl;
    /**
     * 消息发送状态 0:发送中；1：发送失败：2：发送成功
     */
    private String status;
    /**
     * 消息重发次数
     */
    private Integer retryNum;
    /**
     * 消息发送时间
     */
    private Date sendTime;
    /**
     * 消息更新时间
     */
    private Date updateTime;
    /**
     * 消息分组
     */
    @TableField(exist = false)
    private GroupEnum groupEnum;
    /**
     * 消息业务类型
     */
    @TableField(exist = false)
    private TagEnum tagEnum;
    /**
     * appDataEX消息业务类型
     */
    @TableField(exist = false)
    private SzjyTagEnum szjyTagEnum;
    /**
     * 消息主题
     */
    @TableField(exist = false)
    private String topic;
    /**
     * 消息分组
     */
    @TableField(exist = false)
    private String group;
    /**
     * 消息业务类型
     */
    @TableField(exist = false)
    private String tag;

    public MqMessage(){}

    public MqMessage(String key,String replyId,String replyContent,String toId, String title, String content, String appId, String type, String noticeType, String detailUrl, String status, Integer retryNum, Date sendTime, Date updateTime, GroupEnum groupEnum, TagEnum tagEnum,SzjyTagEnum szjyTagEnum) {
        this.key = key;
        this.replyId = replyId;
        this.replyContent = replyContent;
        this.toId = toId;
        this.title = title;
        this.content = content;
        this.appId = appId;
        this.type = type;
        this.noticeType = noticeType;
        this.detailUrl = detailUrl;
        this.status = status;
        this.retryNum = retryNum;
        this.sendTime = sendTime;
        this.updateTime = updateTime;
        this.groupEnum = groupEnum;
        this.tagEnum = tagEnum;
        this.szjyTagEnum = szjyTagEnum;
    }

}
