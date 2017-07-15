package client.listener

import org.apache.mina.core.service.*
import org.apache.mina.core.session.*
import org.slf4j.LoggerFactory

/**
 * Created by guofeng on 2017/6/8.
 */
class ClientListener : IoServiceListener {

    val logger = LoggerFactory.getLogger(javaClass.simpleName)!!

    override fun sessionDestroyed(session: IoSession?) {
        logger.debug("session destroyed")
    }

    override fun serviceActivated(service: IoService?) {
        logger.debug("service activated")
    }

    override fun serviceDeactivated(service: IoService?) {
        logger.debug("service deactivated")
    }

    override fun sessionClosed(session: IoSession?) {
        logger.debug("session closed")
    }

    override fun sessionCreated(session: IoSession?) {
        logger.debug("session created")
    }

    override fun serviceIdle(service: IoService?, idleStatus: IdleStatus?) {
        logger.debug("session idle")
    }

}
