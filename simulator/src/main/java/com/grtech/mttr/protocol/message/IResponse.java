package com.grtech.mttr.protocol.message;

public interface IResponse {
    public void parseResponse(byte[] response)  throws Exception;
}
