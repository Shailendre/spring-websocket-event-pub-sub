package com.alphasense.poc.publish;

import com.alphasense.poc.dto.StreamingData;
import com.alphasense.poc.emitter.DataEmitter;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final Collection<DataEmitter> dataEmitters;


    public void publishData(StreamingData message) {
        publisher.publishEvent(message);
    }

    @PostConstruct
    public void init() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> dataEmitters.forEach(dataEmitter -> {
            try {
                StreamingData data = dataEmitter.emit();
                publishData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }), 1L, 5L, TimeUnit.SECONDS);
    }

}
