package pro.wuan.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"pro.wuan.**.**.*"})
public class GatewayApplication {


	public static void main(String[] args) {
		new SpringApplicationBuilder(GatewayApplication.class).run(args);
	}

}
