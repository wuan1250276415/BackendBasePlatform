package pro.wuan.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;
import pro.wuan.bus.config.MyApplicationEvent;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

    @EventListener
    public void onMyCustomEvent(MyApplicationEvent event) {
        System.out.println("Received event: " + event.getMessage());
    }
}
