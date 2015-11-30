package slimer.dubbo;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import slimer.App;
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
public class TestProvider {
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx =SpringApplication.run(TestProvider.class, args);
		System.out.println("provider start------------------------" + ctx.getDisplayName());
		System.in.read();
	}
}
