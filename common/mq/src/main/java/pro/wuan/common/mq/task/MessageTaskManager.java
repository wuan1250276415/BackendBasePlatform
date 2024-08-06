package pro.wuan.common.mq.task;

import java.util.concurrent.*;

/**
 * 消息任务管理器
 *
 * @author: oldone
 * @date: 2021/9/18 11:06
 */
public class MessageTaskManager {
    /**
     * mq消息任务定时线程池
     */
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    private MessageTaskManager() {

    }

    /**
     * 单例模式获取线程池
     *
     * @return
     */
    public static ScheduledExecutorService getExecutorService() {
        return scheduledThreadPool;
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        scheduledThreadPool.shutdown();
    }
}
