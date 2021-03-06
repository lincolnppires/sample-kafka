package com.github.lincolnppires.sample.kafka.samplekafka.tutorial1.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import java.util.*


/**
 * @author lincoln.pires
 */
@Configuration
class KafkaTopicConfig {

    @Value(value = "\${kafka.bootstrapAddress}")
    private val bootstrapAddress: String? = null

    @Bean
    fun kafkaAdmin(): KafkaAdmin? {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        return KafkaAdmin(configs)
    }

    @Bean
    fun topic1(): NewTopic? {
        return NewTopic("tutorial1-topic", 3, 1.toShort())
    }

}