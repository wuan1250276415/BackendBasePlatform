package pro.wuan.common.mq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: oldone
 * @date: 2021/11/11 17:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupTopicTag {
    /**
     * 消息业务分类标签
     */
    private String tag;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息分组
     */
    private String group;
}
