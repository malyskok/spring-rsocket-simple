/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.malyskok.rsocket;

import com.malyskok.rsocket.dto.ChartResponseDto;
import com.malyskok.rsocket.dto.RequestDto;
import com.malyskok.rsocket.dto.ResponseDto;
import com.malyskok.rsocket.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Lec01RsocketTest {

    RSocket rSocket;

    @BeforeAll
    void beforeAll() {
        this.rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 6565))
                .block();
    }

    @Test
//    @RepeatedTest(3)
    void fireAndForget() {
//        DefaultPayload.create("Hello world!")
        Payload payload = ObjectUtil.toPayload(new RequestDto(5));
        Mono<Void> mono = this.rSocket.fireAndForget(payload);

        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void requestResponse(){
        Payload payload = ObjectUtil.toPayload(new RequestDto(5));
        Mono<ResponseDto> responseDtoMono = this.rSocket.requestResponse(payload)
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(responseDtoMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void requestStream(){
        Payload payload = ObjectUtil.toPayload(new RequestDto(5));
        Flux<ResponseDto> responseDtoFlux = this.rSocket.requestStream(payload)
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println)
                .take(4);

        StepVerifier.create(responseDtoFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void requestChannel(){
        Flux<Payload> requestFlux = Flux.range(-10, 21)
                .map(i -> new RequestDto(i))
                .map(ObjectUtil::toPayload)
                .delayElements(Duration.ofSeconds(1));

        Flux<ChartResponseDto> responseDtoFlux = this.rSocket.requestChannel(requestFlux)
                .map(payload -> ObjectUtil.toObject(payload, ChartResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(responseDtoFlux)
                .expectNextCount(21)
                .verifyComplete();
    }


}