package com.home.ilya.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromServerSentEvents;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.scheduler.Schedulers.elastic;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumerController {

    private static final String TOPIC_NAME = "topicName";
    private final Consumer consumer;

    public RouterFunction<ServerResponse> router() {
        return nest(path("/consumer"), route()
                .POST("/topic/{" + TOPIC_NAME + "}/subscribe", startMessageConsuming())
                .GET("/topic/{" + TOPIC_NAME + "}/enroll",
                        request -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_EVENT_STREAM)
                                .body(fromServerSentEvents(consumer.getServerEvents()))
                )
                .build()
        );
    }

    private HandlerFunction<ServerResponse> startMessageConsuming() {
        return request -> Mono.just(request.pathVariable(TOPIC_NAME))
                .map(consumer::consumeMessages)
                .subscribeOn(elastic())
                .flatMap(disposable -> ok().build());
    }

}
