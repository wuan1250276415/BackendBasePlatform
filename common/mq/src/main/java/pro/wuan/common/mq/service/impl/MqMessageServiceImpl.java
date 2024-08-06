package pro.wuan.common.mq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.mq.mapper.MqMessageMapper;
import pro.wuan.common.mq.model.MqMessage;
import pro.wuan.common.mq.service.MqMessageService;

import java.util.List;

/**
 * mq消息服务实现类
 *
 * @author: oldone
 * @date: 2021/9/17 17:56
 */
@Service
public class MqMessageServiceImpl extends BaseServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

    /**
     * 根据状态获取消息列表
     *
     * @param state
     * @return
     */
    public List<MqMessage> getListByState(String state) {
        QueryWrapper<MqMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", state);
        return this.selectList(queryWrapper);
    }
}
