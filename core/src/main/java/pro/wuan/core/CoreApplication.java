package pro.wuan.core;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(proxyBeanMethods = false)
@EnableDiscoveryClient
@ComponentScan(value = {"pro.wuan.core", "pro.wuan.common.redis", "pro.wuan.common.mq"})
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

	@Bean
    public CommandLineRunner init(final RepositoryService repositoryService,
                                  final RuntimeService runtimeService,
                                  final TaskService taskService) {

        return strings -> {
			System.out.println("Number of process definitions : "
				+ repositoryService.createProcessDefinitionQuery().count());
			System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
			runtimeService.startProcessInstanceByKey("oneTaskProcess");
			System.out.println("Number of tasks after process start: "
				+ taskService.createTaskQuery().count());
		};
    }
}
