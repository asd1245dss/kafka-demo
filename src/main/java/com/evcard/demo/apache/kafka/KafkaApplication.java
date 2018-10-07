package com.evcard.demo.apache.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
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
        kafkaTemplate.send("foo1", "foo", "foo1");
        kafkaTemplate.send("foo2", "foo", "foo2");
        kafkaTemplate.send("foo3", "foo", "foo3");

        latch.await(60, TimeUnit.SECONDS);
        LOG_UTIL.info("All received");
    }

    @KafkaListener(topics = {"test", "foo1", "foo2", "foo3"})
    public void listen(ConsumerRecord<String, String> record) {
        LOG_UTIL.info("receive => {}", record);
        latch.countDown();
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configMap = new HashMap<>(1);
        configMap.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092");
        return new KafkaAdmin(configMap);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic("foo1", 1, (short) 1);
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic("foo2", 1, (short) 1);
    }

    @Bean
    public NewTopic topic3() {
        return new NewTopic("foo3", 1, (short) 1);
    }
}
