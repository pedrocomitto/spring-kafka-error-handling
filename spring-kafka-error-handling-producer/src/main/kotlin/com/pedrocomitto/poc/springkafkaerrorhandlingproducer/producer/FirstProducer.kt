package com.pedrocomitto.poc.springkafkaerrorhandlingproducer.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class FirstProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    companion object {
        const val TOPIC_NAME = "first.topic"
    }

    fun produce(key: String, value: String) {
        val record = ProducerRecord(TOPIC_NAME, key, value)

        kafkaTemplate.send(record)
    }

}