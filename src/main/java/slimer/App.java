package slimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import slimer.redismessage.MessageSender;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
public class App {

	public static void main(String[] args) {
		ApplicationContext ctx =SpringApplication.run(App.class, args);
		MessageSender sender=ctx.getBean(MessageSender.class);
		sender.sendMessage("test redis");
	}

}
