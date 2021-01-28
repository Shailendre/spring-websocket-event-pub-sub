package com.alphasense.poc.emitter;

import com.alphasense.poc.dto.StreamingData;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DateEmitterA implements DataEmitter {

    private static final String ID = "12345";

    @Override
    public StreamingData emit() throws Exception {
        File file = new File(ID.concat(".txt"));
        String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        StreamingData streamingData = new StreamingData();
        streamingData.setData(data);
        streamingData.setId(ID);
        streamingData.setType("message_from_server");

        return streamingData;
    }

    @Override
    public void writeDataSilently() throws Exception {

        File file = new File(ID.concat(".txt"));
        FileUtils.writeStringToFile(file, "File ".concat(ID).concat(" ").concat(LocalDateTime.now() + "\n"), StandardCharsets.UTF_8, true);

    }

    @PostConstruct
    public void init() {
        log.info("Starting DataEmitterA Scheduler!");
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            try {
                writeDataSilently();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1L, 3L, TimeUnit.SECONDS);
    }
}
