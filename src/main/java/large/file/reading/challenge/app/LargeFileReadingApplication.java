package large.file.reading.challenge.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Jarosław Kormański
 */
@SpringBootApplication
@EnableScheduling
public class LargeFileReadingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LargeFileReadingApplication.class, args);
	}

}
