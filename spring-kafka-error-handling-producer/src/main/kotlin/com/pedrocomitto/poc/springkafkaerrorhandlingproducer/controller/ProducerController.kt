package com.pedrocomitto.poc.springkafkaerrorhandlingproducer.controller

import com.pedrocomitto.poc.springkafkaerrorhandlingproducer.producer.FirstProducer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/first")
class ProducerController(
    private val fistProducer: FirstProducer
) {

    @GetMapping("/{key}/{value}") // for testing purposes only
    fun produce(@PathVariable key: String, @PathVariable value: String) =
        fistProducer.produce(key, value)

}