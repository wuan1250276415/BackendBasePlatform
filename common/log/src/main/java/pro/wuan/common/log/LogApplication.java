package pro.wuan.common.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class LogApplication {

	public static void main(String[] args) {
		log.info("LogApplication is starting...");
		SpringApplication.run(LogApplication.class, args);
	}

}
