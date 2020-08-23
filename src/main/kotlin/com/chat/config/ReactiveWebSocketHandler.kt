package com.chat.config

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.time.Duration
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID
import java.util.function.BiFunction


@Component("ReactiveWebSocketHandler")
open class ReactiveWebSocketHandler :WebSocketHandler {

    private val json = ObjectMapper()

    private val eventFlux =
        Flux.generate { sink: SynchronousSink<String?> ->
            val event = Event(randomUUID().toString(), now().toString())
            try {
                sink.next(json.writeValueAsString(event))
            } catch (e: JsonProcessingException) {
                sink.error(e)
            }
        }

    private val intervalFlux = Flux.interval(Duration.ofMillis(1000L))
        .zipWith(eventFlux,
            BiFunction { time: Long?, event: String? -> event }
        )

    override fun handle(webSocketSession: WebSocketSession): Mono<Void?>? {
        return webSocketSession.send(intervalFlux
                .map { payload: String? ->
                    webSocketSession.textMessage(
                        payload
                    )
                }
            ).and(webSocketSession.receive()
                .map { obj: WebSocketMessage -> obj.payloadAsText }
                .log()
            )
    }

}