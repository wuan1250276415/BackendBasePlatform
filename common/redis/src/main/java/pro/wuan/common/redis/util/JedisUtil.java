package pro.wuan.common.redis.util;

import cn.hutool.core.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Configuration
public class JedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(JedisUtil.class);

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            try {
                redisTemplate.delete(key);
            } catch (Exception ex) {
                log.warn("删除redis的key[" + key + "]出现异常,", ex);
            }

        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 多key值获取读取缓存
     *
     * @param keys
     * @return
     */
    public List<Object> mutilGet(final List<String> keys) {
        List<Object> result = null;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        result = operations.multiGet(keys);
        return result;
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 从左列表添加
     *
     * @param k
     * @param v
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(k, v);
    }

    /**
     * 列表添加
     *
     * @param k
     */
    public void lPop(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPop(k);
    }

    /**
     * 从右列表添加
     *
     * @param k
     * @param v
     */
    public void rPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * 从右列表删除
     *
     * @param k
     */
    public void rPop(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPop(k);
    }

    /**
     * 从右列表删除
     *
     * @param k
     */
    public void removeKey(String k, String v) {
        redisTemplate.opsForList().remove(k, -1, v);
    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 清空列表
     *
     * @param k
     * @return
     */
    public void ltrim(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.trim(k, -1, -2);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 删除锁
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 模糊匹配
     *
     * @param key
     */
    public Set<String> getKeys(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        return keys;
    }

    /**
     * 哈希 添加 (H key, HK hashKey, HV value)
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value, Long expireTime) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
        if(ObjectUtil.isNotEmpty(expireTime)){
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        }
    }
    /**
     * 哈希 删除
     *
     * @param key
     * @param hashKey
     */
    public Long hmDelete(String key, Object... hashKey) {
        Long i = redisTemplate.opsForHash().delete(key, hashKey);
        return i;
    }

    /**
     * 哈希 判断hashKey是否存在
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Boolean hmhasKey(String key, Object hashKey) {
        Boolean aBoolean = redisTemplate.opsForHash().hasKey(key, hashKey);
        return aBoolean;
    }
    /**
     * 哈希 获取key对应的map中所有的值
     *
     * @param key
     * @return
     */
    public List hmGetValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }
    /**
     * 哈希 获取key对应的中所有的
     *
     * @param key
     * @return
     */
    public Set<Object> hmGetKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 哈希 increment
     *
     * @param key
     * @return
     */
    public long hmIncrement(String key,Object hashKey,long data) {
        return redisTemplate.opsForHash().increment(key,hashKey,data);
    }

}
