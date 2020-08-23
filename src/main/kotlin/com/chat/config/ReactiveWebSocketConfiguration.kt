package com.chat.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import java.util.*

@Configuration
open class ReactiveWebSocketConfiguration (private val webSocketHandler: ReactiveWebSocketHandler){

    @Bean
    open fun webSocketHandlerMapping(): HandlerMapping? {
        val map: MutableMap<String, WebSocketHandler?> =
            HashMap()
        map["/event-emitter"] = webSocketHandler
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 1
        handlerMapping.urlMap = map
        return handlerMapping
    }

    @Bean
    open fun handlerAdapter(): WebSocketHandlerAdapter? {
        return WebSocketHandlerAdapter()
    }
}