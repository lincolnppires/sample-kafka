package com.github.lincolnppires.sample.kafka.samplekafka.tutorial1.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.PartitionOffset
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service


/**
 * @author lincoln.pires
 */
@Service
class Consumer {

    @KafkaListener(topics = ["tutorial1-topic"], groupId = "tutorial1-group")
    fun listen(message: String) {
        println("Received Message in group foo: $message")
    }

    @KafkaListener(topics = ["tutorial1-topic"], groupId = "tutorial1-group")
    fun listenWithHeaders(@Payload message: String,
                          @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition: Int) {

        println("Received Message with headers: $message from partition: $partition")
    }

    //consuming messages from a specific partition
    @KafkaListener(topicPartitions = [TopicPartition(topic = "tutorial1-topic",
                        partitionOffsets = [
                            PartitionOffset(partition = "0", initialOffset = "0"),
                            PartitionOffset(partition = "2", initialOffset = "0") ])],
                    groupId = "tutorial1-group-specific-partition"
    )
    fun listenToPartition(@Payload message: String,
                          @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition: Int) {

        println("Received Message from a specific partition: $message from partition: $partition")
    }

    //adding message filter for listeners
    @KafkaListener(topics = ["tutorial1-topic"], containerFactory = "filterKafkaListenerContainerFactory", groupId = "tutorial1-group-filter")
    fun listenWithFilter(message: String) {
        println("Received Message in filtered listener: $message")
    }
}