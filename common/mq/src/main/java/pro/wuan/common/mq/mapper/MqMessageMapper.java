package pro.wuan.common.mq.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.mq.model.MqMessage;

/**
 * mq消息mapper
 *
 * @author: oldone
 * @date: 2021/9/17 17:48
 */
@Mapper
public interface MqMessageMapper extends IBaseMapper<MqMessage> {
}
