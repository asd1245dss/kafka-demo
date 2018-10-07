package com.evcard.demo.apache.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args).close();
    }

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private final CountDownLatch latch = new CountDownLatch(3);

    private static final Logger LOG_UTIL = LoggerFactory.getLogger(KafkaApplication.class);

    @Override
    public void run(String... args) throws Exception {
        kafkaTemplate.send("test", "foo", "foo1");
        kafkaTemplate.send("test", "foo", "foo2");
        kafkaTemplate.send("test", "foo", "foo3");

        latch.await(60, TimeUnit.SECONDS);
        LOG_UTIL.info("All received");
    }

    @KafkaListener(topics = "test")
    public void listen(ConsumerRecord<String, String> record) {
        LOG_UTIL.info("receive => {}", record);
        latch.countDown();
    }
}
