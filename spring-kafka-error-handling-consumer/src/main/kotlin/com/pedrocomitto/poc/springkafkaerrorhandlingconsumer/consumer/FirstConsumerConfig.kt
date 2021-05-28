package com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.consumer

import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.exception.DisposableException
import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.exception.NonRetryableException
import com.pedrocomitto.poc.springkafkaerrorhandlingconsumer.producer.FallbackComponent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.ErrorHandler
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.RetryListener
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.retry.support.RetryTemplate
import org.springframework.util.backoff.ExponentialBackOff
import org.springframework.util.backoff.FixedBackOff
import java.awt.Container
import java.lang.IllegalArgumentException
import java.util.function.BiFunction

@Configuration
class FirstConsumerConfig(
    private val fallbackComponent: FallbackComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun firstContainer(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()

        factory.consumerFactory = DefaultKafkaConsumerFactory(consumerProperties())
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD // use AckMode.RECORD to commit each record individually
        factory.setErrorHandler(customErrorHandler())

        return factory
    }

    private fun customErrorHandler(): SeekToCurrentErrorHandler {
        return SeekToCurrentErrorHandler({ record: ConsumerRecord<*, *>, ex: Exception ->
            log.info("Recovering record, record=$record, exception=${ex.cause}")

            if (ex.cause !is DisposableException) {

                try {
                    fallbackComponent.doSomething()
                } catch (ex: Exception) {
                    log.error("Recovery failed, implement a fallback here")
                }

            } else {
                log.warn("DisposableException thrown, discarding. message=${ex.message}")
            }

        }, ExponentialBackOff(100, 1.5)
                .apply { this.maxInterval = 300000 }
                .apply { this.maxElapsedTime = 600000 })
            .apply { this.addNotRetryableExceptions(NonRetryableException::class.java) }
            .apply { this.setRetryListeners(FirstRetryListener()) }
    }

    private fun consumerProperties(): Map<String, Any> {
        val props = HashMap<String, Any>()

        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "spring-kafka-error-handling"
        props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG]

        return props
    }

}

private class FirstRetryListener : RetryListener {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun failedDelivery(record: ConsumerRecord<*, *>, ex: Exception, deliveryAttempt: Int) {
        log.error("Delivery failed")

        // register metrics
    }

    override fun recoveryFailed(
        record: ConsumerRecord<*, *>,
        original: Exception,
        failure: Exception
    ) {
        log.error("Failed to recover, originalException=${original.message}, failureException=${failure.message}")

        // register metrics
    }

    override fun recovered(record: ConsumerRecord<*, *>, ex: Exception) {
        log.info("Recovered with success")

        // register metrics
    }

}
