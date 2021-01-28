package com.lazycompiler.poc.emitter;

import com.lazycompiler.poc.dto.StreamingData;

public interface DataEmitter {

    StreamingData emit() throws Exception;

    void writeDataSilently() throws Exception ;

}
