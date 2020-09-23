package com.github.lincolnppires.sample.kafka.samplekafka.tutorial1.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author lincoln.pires
 */
@RestController
@RequestMapping(value = ["send/message"])
class ProducerController {

    @Autowired
    private val template: KafkaTemplate<String, String>? = null

    @PostMapping(path = arrayOf("{message}"))
    fun sendMesssage(@PathVariable message: String){
        val future = this.template?.send("tutorial1-topic", message)

        future!!.addCallback(object : ListenableFutureCallback<SendResult<String?, String?>?> {

            override fun onSuccess(result: SendResult<String?, String?>?) {
                System.out.println("Sent message=[" + result?.recordMetadata.toString() +
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