package com.chat.config

import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration


open class ReactiveClientWebSocket

fun main() {
    val client: WebSocketClient = ReactorNettyWebSocketClient()
    client.execute(
        URI.create("ws://localhost:8080/event-emitter")
    ) { session: WebSocketSession ->
        session.send(
            Mono.just(session.textMessage("event-spring-reactive-client-websocket"))
        )
            .thenMany(
                session.receive()
                    .map { obj: WebSocketMessage -> obj.payloadAsText }
                    .log()
            )
            .then()
    }
    .block(Duration.ofSeconds(10L))
}