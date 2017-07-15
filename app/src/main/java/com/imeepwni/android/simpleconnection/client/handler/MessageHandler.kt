@file:Suppress("UNCHECKED_CAST")

package client.handler

import client.ClientConfig.eventBus
import client.message.*
import org.apache.mina.core.service.*
import org.apache.mina.core.session.*
import org.slf4j.*

/**
 * Created by guofeng on 2017/6/8.
 */
class MessageHandler : IoHandlerAdapter() {

    val logger = LoggerFactory.getLogger(javaClass.simpleName)!!

    override fun sessionCreated(session: IoSession?) {
        super.sessionCreated(session)
        logger.debug("session created")
    }

    override fun exceptionCaught(session: IoSession?, cause: Throwable) {
        logger.debug("exceptionCaught: ${cause.printStackTrace()}")
        eventBus.post(cause)
    }

    override fun messageReceived(session: IoSession?, message: Any) {
        val msg = message.toString()
        logger.debug("received message $msg")
        eventBus.post(MessageReceived(msg))
    }

    override fun messageSent(session: IoSession?, message: Any) {
        val msg = message.toString()
        logger.debug("sent message $msg")
        eventBus.post(MessageSent(msg))
    }

}