package pro.wuan.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(proxyBeanMethods = false)
@EnableDiscoveryClient
@ComponentScan(value = {"pro.wuan.core", "pro.wuan.common.redis","pro.wuan.feignapi"})
@EntityScan("pro.wuan.feignapi.userapi.entity")
@Slf4j
public class CoreApplication {

	@Value("${spring.rabbitmq.queueName}")
	private String queueName;

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

	/**
	 * Creates a Queue bean.
	 * This queue is used for sending and receiving messages.
	 * @return Queue
	 */
	@Bean
	Queue queue() {
		return new Queue("core");
	}


	/**
	 * Listener for messages from the "queue" queue.
	 * Logs the received message.
	 * @param in the received message
	 */
	@RabbitListener(queues = {"core"})
	void listen(String in) {
		log.info("Mq接收到消息: {}", in);
	}
}
