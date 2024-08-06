package pro.wuan.common.mq.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.mq.mapper.MqMessageMapper;
import pro.wuan.common.mq.model.MqMessage;

import java.util.List;

/** mq消息接口服务
 * @author: oldone
 * @date: 2021/9/17 17:51
 */
public interface MqMessageService extends IBaseService<MqMessageMapper, MqMessage> {

    /**
     * 根据状态获取消息列表
     * @param state
     * @return
     */
    List<MqMessage> getListByState(String state);
}
