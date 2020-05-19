package com.home.ilya.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.scheduler.Schedulers.elastic;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProducerController {

    private static final String TOPIC_NAME = "topicName";
    private final Producer producer;

    public RouterFunction<ServerResponse> router() {
        return nest(path("/producer"), route()
                .POST("/topic/{" + TOPIC_NAME + "}", publishMessage())
                .build()
        );
    }

    private HandlerFunction<ServerResponse> publishMessage() {
        return request -> request.bodyToMono(String.class)
                .flatMap(message -> producer.publish(request.pathVariable(TOPIC_NAME), message))
                .subscribeOn(elastic())
                .transform(uuid -> ok().body(uuid, UUID.class));
    }
}
