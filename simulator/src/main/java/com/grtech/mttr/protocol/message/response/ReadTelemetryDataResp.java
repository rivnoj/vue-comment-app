package com.grtech.mttr.protocol.message.response;

import com.grtech.mttr.protocol.message.IResponse;
import com.grtech.util.GeneralFunctions;

/**
 * ReadTelemetryDataResp
 */
public class ReadTelemetryDataResp implements IResponse {
    private byte address;
    private byte addressResp;
    private Integer temperature;
    private Integer voltage;
    private Integer conductance;

    public ReadTelemetryDataResp(Byte address) {
        if (address <= 2) {
            throw new IllegalArgumentException("Enderço deve ser maior que 2.");
        }

        this.address = address;
    }

    public void parseResponse(byte[] response) throws Exception {
        if ((response!= null)&&(response.length >= 11)) {
            if (response[0] == ((byte)0xaa)) {
                this.addressResp = response[2];

                if (this.addressResp == address) {
                    short crc = GeneralFunctions.crc16DNP(new byte[] {response[3], response[4], response[5], response[6], response[8], response[8]}, true, true);
                    
                    if ((((byte)(crc&0xff)) == response[10])&&(((byte)((crc>>8)&0xff)) == response[9])) {
                        this.temperature = ((response[3]<<8)&0xff00)|(response[4]&0xff);
                        this.voltage = ((response[5]<<8)&0xff00)|(response[6]&0xff);
                        this.conductance = ((response[7]<<8)&0xff00)|(response[8]&0xff);
                    } else {
                        throw new Exception("CRC da resposta é inválido.");    
                    }
                } else {
                    throw new Exception("Endereço da resposta não corresponde ao da requisição.");    
                }
            } else {
                throw new Exception("Byte de start do protocolo não encontrado na resposta.");    
            }
        } else {
            throw new Exception("Quantidade de bytes na resposta não é o esperado.");
        }
    }

    public byte getAddress() {
        return address;
    }

    public Double getTemperature() {
        return (double) temperature;
    }

    public Double getVoltage() {
        return (voltage*28.75)/1000;
    }

    public Double getConductance() {
        return 6.0*conductance;
    }
}