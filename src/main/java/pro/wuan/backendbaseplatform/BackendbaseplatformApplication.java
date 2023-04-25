package pro.wuan.backendbaseplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BackendbaseplatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendbaseplatformApplication.class, args);
	}

}
