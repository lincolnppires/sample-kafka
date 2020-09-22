package com.github.lincolnppires.sample.kafka.samplekafka.tutorial1.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author lincoln.pires
 */
@RestController
@RequestMapping(value = ["producers"])
class ProducerController {

    @Autowired
    private val template: KafkaTemplate<String, String>? = null

    @PostMapping
    fun sendMesssage(){
        val future = this.template?.send("tutorial1-topic", "teste1")

        future!!.addCallback(object : ListenableFutureCallback<SendResult<String?, String?>?> {

            override fun onSuccess(result: SendResult<String?, String?>?) {
                System.out.println("Sent message=[" + "teste1" +
                        "] with offset=[" + result?.recordMetadata?.offset().toString() + "]" + "with partition=[" +
                        result?.recordMetadata?.partition() + "]")
            }

            override fun onFailure(ex: Throwable) {
                System.out.println(("Unable to send message=["
                        + "teste1") + "] due to : " + ex.message)
            }
        })
    }

}