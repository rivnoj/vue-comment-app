package com.grtech.mttr.protocol.message;

import com.grtech.util.GeneralFunctions;

public class Request {
    public static final byte READ_TELEMETRY_DATA = 'M';
    public static final byte CALCULATE_CONDUCTANCE = 'R';
    public static final byte SAVE_CONDUCTANCE_FACTOR = 'G';

    public byte address;
    public byte commandID;

    public Request(byte address, byte commandID) {
        if (address <= 2) {
            throw new IllegalArgumentException("Enderço deve ser maior que 2.");
        }

        if ((commandID != READ_TELEMETRY_DATA) &&
            (commandID != CALCULATE_CONDUCTANCE) &&
            (commandID != SAVE_CONDUCTANCE_FACTOR)) {
                throw new IllegalArgumentException("Comando inválido.");
        }

        this.address = address;
        this.commandID = commandID;
    }

    public byte[] getBytesCommand() {
        byte[] res = new byte[8];
        res[0] = (byte) 0xaa;
        res[1] = 0;
        res[2] = this.address;
        res[3] = this.commandID;
        res[4] = 0x00;
        res[5] = 0x00;
        short crc = GeneralFunctions.crc16DNP(new byte[] { res[2], res[3], res[4], res[5] }, true, true);
        res[6] = (byte) (crc >> 8);
        res[7] = (byte) crc;
        return res;
    }
}
