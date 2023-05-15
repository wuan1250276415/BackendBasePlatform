package pro.wuan.bus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@RemoteApplicationEventScan(basePackages = "pro.wuan.bus.config") // 扫描自定义的事件
@EnableDiscoveryClient // 开启服务发现
public class BusApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusApplication.class, args);
    }


}
