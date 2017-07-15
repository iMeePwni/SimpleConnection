package client.handler

import org.apache.mina.core.session.*
import org.apache.mina.filter.keepalive.*

/**
 * Created by guofeng on 2017/6/7.
 */
class HeartBeatHandler : KeepAliveMessageFactory {
    val heartBeat = "HB"

    override fun getResponse(session: IoSession?, request: Any?) = null

    override fun isRequest(session: IoSession?, message: Any?): Boolean = false

    override fun isResponse(session: IoSession?, message: Any?): Boolean = heartBeat==message.toString()

    override fun getRequest(session: IoSession?) = heartBeat


}