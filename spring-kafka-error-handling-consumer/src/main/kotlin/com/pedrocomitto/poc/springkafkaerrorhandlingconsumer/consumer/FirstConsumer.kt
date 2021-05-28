package com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.consumer

import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.exception.DisposableException
import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.exception.NonRetryableException
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class FirstConsumer {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val TOPIC_NAME = "first.topic"
    }

    @KafkaListener(topics = [TOPIC_NAME], containerFactory = "firstContainer")
    fun consume(record: ConsumerRecord<String, String>) {
        log.info("M=consume, I=consuming message, record=$record")
        throw RuntimeException()
//        throw NonRetryableException() // you may throw a NonRetryableException to bypass the retries
//        throw DisposableException() // you may throw a DisposableException to discard the message
    }

}