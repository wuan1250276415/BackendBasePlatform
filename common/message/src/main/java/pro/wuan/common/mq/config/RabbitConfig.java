package pro.wuan.common.mq.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * RabbitMQ's configuration class.
 * This class is responsible for setting up the RabbitMQ connection and configuring the RabbitTemplate.
 */
@Configuration
@Slf4j
public class RabbitConfig {

    // RabbitMQ server address
    @Value("${spring.rabbitmq.host}")
    private String addresses;

    // RabbitMQ server username
    @Value("${spring.rabbitmq.username}")
    private String username;

    // RabbitMQ server password
    @Value("${spring.rabbitmq.password}")
    private String password;

    // RabbitMQ server port
    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.virtual-host}")
    private String vhost;

    /**
     * Creates a PooledChannelConnectionFactory bean.
     * This factory is responsible for creating connections to the RabbitMQ server.
     * @return PooledChannelConnectionFactory
     */
    @Bean
    PooledChannelConnectionFactory pcf() {
        ConnectionFactory rabbitConnectionFactory = new ConnectionFactory();
        rabbitConnectionFactory.setHost(addresses);
        rabbitConnectionFactory.setUsername(username);
        rabbitConnectionFactory.setPassword(password);
        rabbitConnectionFactory.setPort(port);
        rabbitConnectionFactory.setVirtualHost(vhost);
        return new PooledChannelConnectionFactory(rabbitConnectionFactory);
    }

    /**
     * Creates a RabbitTemplate bean.
     * This template is used for sending and receiving messages to/from RabbitMQ.
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate()  {
        RabbitTemplate template = new RabbitTemplate(pcf());
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        return template;
    }

    /**
     * Creates a TaskExecutor bean.
     * This executor is used for executing tasks in separate threads.
     * @return TaskExecutor
     */
    @Bean
    TaskExecutor exec() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(10);
        return exec;
    }

    /**
     * Listener for messages from the "queue" queue.
     * Logs the received message.
     * @param in the received message
     */
    @RabbitListener(queues = {"core","queue"})
    void listen(String in) {
        log.info("Mq接收到消息: {}", in);
    }

    /**
     * Creates a Queue bean.
     * This queue is used for sending and receiving messages.
     * @return Queue
     */
    @Bean
    Queue queue() {
        return new Queue("queue");
    }

}