package com.app.myworld;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MyworldApplicationTests.TestMailConfig.class)
class MyworldApplicationTests {

	@Configuration
	static class TestMailConfig {
		@Bean
		JavaMailSender javaMailSender() {
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost("localhost");
			sender.setPort(25);
			return sender;
		}
	}

	@Test
	void contextLoads() {
	}

}
