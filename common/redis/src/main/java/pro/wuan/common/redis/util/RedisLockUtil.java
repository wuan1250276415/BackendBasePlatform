package pro.wuan.common.redis.util;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import pro.wuan.common.core.utils.SpringUtil;

import java.util.concurrent.TimeUnit;

/**
 * @description: Redis分布式锁 工具类--redisson实现
 * @author: oldone
 * @date: 2020/7/27 19:06
 */
public class RedisLockUtil {
    private static final String REDISSON_NAME="redisson";
    //初始化redisson对象
    private static Redisson redisson= SpringUtil.getBean(REDISSON_NAME,Redisson.class);
    /**
     * 根据name对进行上锁操作，redisson Lock 一直等待获取锁
     * @param lockname
     */
    public static void lock(String lockname) throws InterruptedException {
        String key = lockname;
        RLock lock = redisson.getLock(key);
        //lock提供带timeout参数，timeout结束强制解锁，防止死锁
        lock.lock(60L, TimeUnit.SECONDS);
    }

    /**
     * 根据name对进行上锁操作，redisson Lock 一直等待获取锁
     * @param lockname
     */
    public static void lock(String lockname,Long waitTime) throws InterruptedException {
        String key = lockname;
        RLock lock = redisson.getLock(key);
        //lock提供带timeout参数，timeout结束强制解锁，防止死锁
        lock.lock(waitTime, TimeUnit.SECONDS);
    }

    /**
     * 根据name对进行上锁操作，redisson tryLock  根据第一个参数，一定时间内为获取到锁，则不再等待直接返回boolean。交给上层处理
     * @param lockname
     */
    public static boolean tryLock(String lockname) throws InterruptedException {
        String key = lockname;
        RLock lock = redisson.getLock(key);
        //tryLock，第一个参数是等待时间，5秒内获取不到锁，则直接返回。 第二个参数 60是60秒后强制释放
        return lock.tryLock(5L,60L, TimeUnit.SECONDS);
    }

    /**
     * 根据name对进行上锁操作，redisson tryLock  根据第一个参数，一定时间内为获取到锁，则不再等待直接返回boolean。交给上层处理
     * @param lockname
     */
    public static boolean tryLock(String lockname,Long waitTime) throws InterruptedException {
        String key = lockname;
        RLock lock = redisson.getLock(key);
        //tryLock，第一个参数是等待时间，5秒内获取不到锁，则直接返回。 第二个参数 60是60秒后强制释放
        return lock.tryLock(waitTime,60L, TimeUnit.SECONDS);
    }


    /**
     * 根据name对进行解锁操作
     * @param lockname
     */
    public static void unlock(String lockname){
        String key = lockname;
        RLock lock = redisson.getLock(key);
        lock.unlock();
    }
}
