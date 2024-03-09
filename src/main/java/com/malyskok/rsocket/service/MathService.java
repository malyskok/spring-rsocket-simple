/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package com.malyskok.rsocket.service;

import com.malyskok.rsocket.dto.ChartResponseDto;
import com.malyskok.rsocket.dto.RequestDto;
import com.malyskok.rsocket.dto.ResponseDto;
import com.malyskok.rsocket.util.ObjectUtil;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MathService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Receiving: " + ObjectUtil.toObject(payload, RequestDto.class));
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        return Mono.fromSupplier(() -> {
            RequestDto request = ObjectUtil.toObject(payload, RequestDto.class);
            int result = request.getInput() * request.getInput();
            ResponseDto response = new ResponseDto(request.getInput(), result);
            return ObjectUtil.toPayload(response);
        });
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        return Flux.range(1, 10)
                .map(i -> i * requestDto.getInput())
                .map(result -> new ResponseDto(requestDto.getInput(), result))
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(System.out::println)
                .doFinally(signalType -> System.out.println(signalType))
                .map(ObjectUtil::toPayload);
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads)
                .map(payload -> ObjectUtil.toObject(payload, RequestDto.class))
                .map(requestDto -> requestDto.getInput())
                .map(i -> new ChartResponseDto(i, i * i + 1))
                .map(ObjectUtil::toPayload);
    }
}