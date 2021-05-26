package com.pedrocomitto.poc.springkafkaerrorhandlingproducer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKafkaErrorHandlingProducerApplication

fun main(args: Array<String>) {
	runApplication<SpringKafkaErrorHandlingProducerApplication>(*args)
}
