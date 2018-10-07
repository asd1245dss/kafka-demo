package com.evcard.demo.apache.kafka.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ChangWei Li
 * @version 2018-10-05 09:05
 */
@Slf4j
public class KafkaConnectTest {

    @Test
    public void consume() {
        Properties properties = new Properties();
        Map<String, String> paraMap = new HashMap<>(4);
        paraMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092");
        paraMap.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        paraMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        paraMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.putAll(paraMap);
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singleton("test"));


        while (true) {
            for (ConsumerRecord<String, String> record : kafkaConsumer.poll(500)) {
                log.debug("receive message => {}", record);
            }
        }
    }

    @Test
    public void produce() {
        Properties properties = new Properties();
        Map<String, String> paraMap = new HashMap<>(3);
        paraMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092");
        paraMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        paraMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.putAll(paraMap);
        try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)) {
            kafkaProducer.send(new ProducerRecord<>("test", "test"));
        }
    }

    @Test
    public void admin() throws ExecutionException, InterruptedException, TimeoutException {
        Properties properties = new Properties();
        Map<String, String> paraMap = new HashMap<>(3);
        paraMap.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092");
        properties.putAll(paraMap);

        AdminClient kafkaAdminClient = AdminClient.create(properties);
        DeleteTopicsResult result = kafkaAdminClient.deleteTopics(Arrays.asList("foo1", "foo2", "foo3"));

        result.all().get(5, TimeUnit.SECONDS);
    }

}
