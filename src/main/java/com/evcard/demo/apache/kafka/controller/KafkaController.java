package com.evcard.demo.apache.kafka.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ChangWei Li
 * @version 2018-10-02 17:20
 */
@Component
public class KafkaController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

}
