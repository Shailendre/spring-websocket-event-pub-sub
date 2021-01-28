package com.alphasense.poc.emitter;

import com.alphasense.poc.dto.StreamingData;

public interface DataEmitter {

    StreamingData emit() throws Exception;

    void writeDataSilently() throws Exception ;

}
