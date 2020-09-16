package com.github.lincolnppires.sample.kafka.samplekafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleKafkaApplication

fun main(args: Array<String>) {
	runApplication<SampleKafkaApplication>(*args)
}
