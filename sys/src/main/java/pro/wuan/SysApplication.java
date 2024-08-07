package pro.wuan;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"pro.wuan.**.**.*","pro.wuan.*","pro.wuan.**.*"})
public class SysApplication {


	public static void main(String[] args)  {

		new SpringApplicationBuilder(SysApplication.class).run(args);
	}

}
