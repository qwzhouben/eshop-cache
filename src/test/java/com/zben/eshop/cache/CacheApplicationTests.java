package com.zben.eshop.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheApplicationTests {

	@Autowired
	KafkaTemplate kafkaTemplate;

	@Test
	public void contextLoads() throws InterruptedException {
		kafkaTemplate.send("eshop-cache-message", "{\"serviceId\":\"shopInfoService\",\"shopId\":\"1\"}");
		System.out.println("send ok");
		Thread.sleep(5000);
	}

}
