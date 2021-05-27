package com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.producer

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FakeRabbitProducer {

    private val log = LoggerFactory.getLogger(javaClass)

    fun produce() {
        log.info("producing on RabbitMQ")
    }

}