package pro.wuan.organmodelsetting.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.constant.RedisConstant;
import pro.wuan.common.db.config.SqlDefualtAdditionalConfiguration;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.organmodelsetting.mapper.OrgOrganModelSettingMapper;
import pro.wuan.organmodelsetting.model.OrgOrganModelSettingModel;
import pro.wuan.organmodelsetting.service.IOrgOrganModelSettingService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 查询模式数据配置
 * @program: tellhowcloud
 * @author libra
 * @create 2024-01-22 11:48:48
 */
@Service("orgOrganModelSettingService")
@Slf4j
public class OrgOrganModelSettingServiceImpl extends BaseServiceImpl<OrgOrganModelSettingMapper, OrgOrganModelSettingModel> implements IOrgOrganModelSettingService {

    @Autowired
    private Redisson redisson;
    @Autowired
    private SqlDefualtAdditionalConfiguration additionalConfiguration;
    @Autowired
    private JedisUtil jedisUtil;
    /**
     * 根据单位id获取该单位所属数据库模式
     *
     */
    @Override
    public String getOrganDabaseModel(Long organId) {
        Object rediosModl = jedisUtil.hmGet(RedisConstant.SUB_ORGAN_CHANGE_KEY,String.valueOf(organId));
        if(additionalConfiguration.getIsEnableOrganDatabaseModel() && ObjectUtil.isNotEmpty(rediosModl)){
            return (String) rediosModl;
        }
        String lockStr = "ORGORGANMODELSETTING"+organId;
        //加锁防止并发错误，一定程度防止并发竞争-构建分布式锁
        RLock browseLock = redisson.getLock(lockStr);
        String mode = null;
        try {
            //加锁
            browseLock.lock(3, TimeUnit.SECONDS);
            OrgOrganModelSettingModel model = this.selectOne(new LambdaQueryWrapper<OrgOrganModelSettingModel>().like(OrgOrganModelSettingModel::getOrganIds,organId)
                    .orderByDesc(OrgOrganModelSettingModel::getUpdateTime).last("limit 1"));
            if(ObjectUtil.isNotEmpty(model) && additionalConfiguration.getIsEnableOrganDatabaseModel()){
                mode = model.getMode();
                jedisUtil.hmSet(RedisConstant.SUB_ORGAN_CHANGE_KEY,String.valueOf(organId),mode);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            //如果锁被当前线程持有，则释放
            if(browseLock.isLocked() && browseLock.isHeldByCurrentThread()){
                //解锁
                browseLock.unlock();
            }

        }
        return mode;
    }

    /**
     * 获取所有配置的数据库模式
     */
    @Override
    public List<String> getAllDabaseMode() {
        QueryWrapper<OrgOrganModelSettingModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct mode");
        List<OrgOrganModelSettingModel> models = this.selectList(queryWrapper);
        if(CollectionUtil.isNotEmpty(models)){
            return models.stream().map(model -> model.getMode()).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }


}
