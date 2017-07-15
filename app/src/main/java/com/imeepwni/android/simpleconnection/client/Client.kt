package client

import client.Exception.*
import client.handler.*
import client.listener.*
import org.apache.mina.core.session.*
import org.apache.mina.filter.codec.*
import org.apache.mina.filter.codec.textline.*
import org.apache.mina.filter.keepalive.*
import org.apache.mina.transport.socket.nio.*
import org.slf4j.*
import java.net.*

/**
 * Created by guofeng on 2017/6/7.
 */
class Client {

    private val logger = LoggerFactory.getLogger(javaClass.simpleName)!!
    private val connector: NioSocketConnector by lazy { NioSocketConnector() }
    private val clientListener: ClientListener by lazy { ClientListener() }
    private val codecFilter: ProtocolCodecFilter by lazy {
        ProtocolCodecFilter(TextLineCodecFactory(Charsets.UTF_8, LineDelimiter.UNIX.value, LineDelimiter.UNIX.value))
    }
    private val heartFilter: KeepAliveFilter by lazy {
        val keepAliveFilter = KeepAliveFilter(HeartBeatHandler(), IdleStatus.BOTH_IDLE, KeepAliveRequestTimeoutHandler.CLOSE)
        keepAliveFilter.apply {
            isForwardEvent = true
            requestInterval = 30
            requestTimeout = 60
        }
    }
    private var session: IoSession? = null

    fun init() {
        if (connector.isActive) return
        connector.run {
            connectTimeoutMillis = 10_1000
            setDefaultRemoteAddress(InetSocketAddress("123.xx.xxx.xxx", 8080))
            handler = MessageHandler()
            addListener(clientListener)
        }

        val filterChain = connector.filterChain
        if (filterChain.contains("codec")) return
        filterChain.addLast("codec", codecFilter)

        if (filterChain.contains("heart")) return
        filterChain.addLast("heart", heartFilter)
    }

    fun createSession() {
        if (isSessionAvailable()) {
            logger.debug("createSession : session is already available")
            return
        }
        Thread(Runnable {
            try {
                val future = connector.connect()
                future.awaitUninterruptibly()
                session = future.session
            } catch (e: Exception) {
                logger.debug("createSession : ${e.printStackTrace()}")
                ClientConfig.eventBus.post(CreateSessionFailException())
            }
        }).start()
    }

    fun isSessionAvailable(): Boolean
            = !(session==null
            || !session!!.isConnected
            || session!!.isClosing)


    fun sendMessage(request: String) {
        if (!isSessionAvailable()) {
            logger.debug("sendMessage : session 不可用 正在重连")
            ClientConfig.eventBus.post(SessionIsNotAvailableException())
        }
        try {
            session?.write(request)
        } catch (e: Exception) {
            logger.debug("sendMessage : ${e.printStackTrace()}")
            ClientConfig.eventBus.post(WriteMessageFailException())
        }
    }

    fun destroySession() {
        if (isSessionAvailable())
            session?.closeNow()
        session = null
        logger.debug("destroySession : done")
    }

}