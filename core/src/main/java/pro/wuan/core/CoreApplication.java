package pro.wuan.core;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(proxyBeanMethods = false)
@EnableDiscoveryClient
@ComponentScan(value = {"pro.wuan.core", "pro.wuan.common.redis"})
public class CoreApplication {

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
}
