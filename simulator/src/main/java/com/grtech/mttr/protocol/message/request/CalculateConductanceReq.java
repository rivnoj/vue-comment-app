package com.grtech.mttr.protocol.message.request;

import com.grtech.mttr.protocol.message.Request;

public class CalculateConductanceReq  extends Request {
    public byte address;

    public CalculateConductanceReq(byte address) {
        super(address, CALCULATE_CONDUCTANCE);
    }
}
