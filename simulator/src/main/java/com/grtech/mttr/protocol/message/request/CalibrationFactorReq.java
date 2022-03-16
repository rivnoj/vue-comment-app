package com.grtech.mttr.protocol.message.request;

import com.grtech.mttr.protocol.message.Request;

/**
 * CalibrationFactorReq
 */
public class CalibrationFactorReq  extends Request {
    public byte address;

    public CalibrationFactorReq(byte address) {
        super(address, SAVE_CONDUCTANCE_FACTOR);
    }
}