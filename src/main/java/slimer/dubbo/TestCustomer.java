package slimer.dubbo;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
public class TestCustomer {
	public static void main(String[] args) throws IOException {
		ApplicationContext ctx =SpringApplication.run(TestCustomer.class, args);
		System.out.println("customer start------------------------" + ctx.getDisplayName());
		System.in.read();
	}
}
