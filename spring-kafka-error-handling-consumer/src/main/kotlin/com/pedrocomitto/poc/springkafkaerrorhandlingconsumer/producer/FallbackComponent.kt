package com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.producer

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FallbackComponent {

    private val log = LoggerFactory.getLogger(javaClass)

    fun doSomething() {
        log.info("fallback method")
    }

}