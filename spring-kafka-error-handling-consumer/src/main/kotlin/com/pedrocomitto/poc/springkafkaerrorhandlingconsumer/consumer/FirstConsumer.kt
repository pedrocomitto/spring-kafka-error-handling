package com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.consumer

import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.exception.DisposableException
import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.exception.NonRetryableException
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.MessageListener
import org.springframework.stereotype.Component

@Component
class FirstConsumer : MessageListener<String, String> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onMessage(data: ConsumerRecord<String, String>) {
        log.info("M=onMessage, I=consuming message, data=$data")
//        throw RuntimeException()
        throw NonRetryableException() // you may throw a NonRetryableException to bypass the retries
//        throw DisposableException() // you may throw a DisposableException to discard the message
    }

}