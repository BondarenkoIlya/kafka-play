package com.home.ilya;

import com.home.ilya.consumer.ConsumerController;
import com.home.ilya.producer.ProducerController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Configuration
@RequiredArgsConstructor
public class Router {

    private final ProducerController producerController;
    private final ConsumerController consumerController;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/consumer/topic/{topicName}/subscribe",
                    method = POST,
                    operation = @Operation(
                            operationId = "consumerPostOperation",
                            parameters = @Parameter(
                                    in = PATH,
                                    name = "topicName",
                                    description = "Topic Name")))
            ,
            @RouterOperation(path = "/producer/topic/{topicName}",
                    method = POST,
                    operation = @Operation(
                            operationId = "producerOperation",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = String.class))),
                            parameters = @Parameter(
                                    in = PATH,
                                    name = "topicName",
                                    description = "Topic Name")))
            ,
            @RouterOperation(path = "/consumer/topic/{topicName}/enroll",
                    method = GET,
                    operation = @Operation(
                            operationId = "consumerGetOperation",
                            parameters = @Parameter(
                                    in = PATH,
                                    name = "topicName",
                                    description = "Topic Name")))

    })
    public RouterFunction<ServerResponse> routes() {
        return producerController.router().and(consumerController.router());
    }

}
