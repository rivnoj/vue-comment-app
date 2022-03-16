package com.grtech.mttr.protocol.message.request;

import com.grtech.mttr.protocol.message.Request;

/**
 * ReadTelemetryDataReq
 */
public class ReadTelemetryDataReq extends Request {
    public byte address;
    
    public ReadTelemetryDataReq(byte address) {
       super(address, READ_TELEMETRY_DATA);
    }
}