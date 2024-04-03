package pro.wuan.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(GatewayApplication.class).run(args);
	}

//    @EventListener
//    public void onMyCustomEvent(MyApplicationEvent event) {
//        System.out.println("Received event: " + event.getMessage());
//    }
}
