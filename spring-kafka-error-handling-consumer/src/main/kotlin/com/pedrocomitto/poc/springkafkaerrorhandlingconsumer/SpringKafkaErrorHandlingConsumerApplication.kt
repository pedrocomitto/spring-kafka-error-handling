package com.pedrocomitto.poc.springkafkaerrorhandlingconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKafkaErrorHandlingConsumerApplication

fun main(args: Array<String>) {
	runApplication<SpringKafkaErrorHandlingConsumerApplication>(*args)
}
