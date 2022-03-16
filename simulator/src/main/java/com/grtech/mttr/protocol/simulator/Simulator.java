package com.grtech.mttr.protocol.simulator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.grtech.comm.ISerialComm;
import com.grtech.comm.serial.SerialComm;
import com.grtech.mttr.protocol.message.Request;
import com.grtech.mttr.protocol.message.IResponse;
import com.grtech.mttr.protocol.message.request.CalculateConductanceReq;
import com.grtech.mttr.protocol.message.request.CalibrationFactorReq;
import com.grtech.mttr.protocol.message.request.ReadTelemetryDataReq;
import com.grtech.mttr.protocol.message.response.ReadTelemetryDataResp;
import com.grtech.util.ByteQueue;
import com.grtech.util.GeneralFunctions;

/**
 * MTTR Protocol Simulator
 */
public final class Simulator {
    static final String TEXTO_EXEMPLO_USO = "\nExemplo de uso:\n\njava -jar mttr-simulator.jar <porta-serial> <endereço do dispositivo> <comando>, onde:\n  <porta-serial> é o identificador da porta serial;\n  <endereço do dispositivo> é o endereço do dispositivo, que deve ser maior que 2;\n  <comando> é o comando que o dispositivo irá executar, sendo:\n    M para ler os dados de temperatura, tensão e condutividade;\n    R para iniciar o processo de cálculo da condutância;\n    G para gravar o fator de calibração da condutância.\n";

    private Simulator() {
    }

    /**
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        // System.out.println(System.getProperty("java.library.path"));

        if (args.length > 0) {
            if ((!args[0].equalsIgnoreCase("-help")) &&
                    (!args[0].equalsIgnoreCase("?")) &&
                    (!args[0].equalsIgnoreCase("/?"))) {
                ISerialComm serial = new SerialComm("1");

                try {
                    if (serial.connect(args[0], 1000)) {
                        serial.clearBuffer();

                        if (args.length > 1) {
                            Byte address = Integer.valueOf(args[1]).byteValue();

                            if (address > 2) {
                                byte command = (byte) args[2].charAt(0);

                                if ((command == Request.CALCULATE_CONDUCTANCE) ||
                                    (command == Request.READ_TELEMETRY_DATA) ||
                                    (command == Request.SAVE_CONDUCTANCE_FACTOR)) {
                                    System.out.println("Iniciando...");
                                    Request request = null;

                                    switch (command) {
                                        case Request.CALCULATE_CONDUCTANCE:
                                            request = new CalculateConductanceReq(address);
                                            break;

                                        case Request.READ_TELEMETRY_DATA:
                                            request = new ReadTelemetryDataReq(address);
                                            break;

                                        case Request.SAVE_CONDUCTANCE_FACTOR:
                                            request = new CalibrationFactorReq(address);
                                            break;
                                    }

                                    if (serial.write(request.getBytesCommand())) {
                                        GeneralFunctions.sleep(1);
                                        
                                        if (command == Request.READ_TELEMETRY_DATA) {
                                            long currtime = System.currentTimeMillis();

                                            while ((System.currentTimeMillis() - currtime < 5000) &&
                                                    (!serial.isDataAvailable())) {
                                                GeneralFunctions.sleep(1);
                                            }

                                            if (serial.isDataAvailable()) {
                                                GeneralFunctions.sleep(100);
                                                ByteQueue bq = new ByteQueue();
                                                serial.readByteAvailable(bq);
                                                
                                                if (bq.size() >= 11) {
                                                    IResponse response = new ReadTelemetryDataResp(address);
                                                    response.parseResponse(bq.popAll());
                                                    Locale loc = new Locale("pt_BR");
                                                    NumberFormat nf = NumberFormat.getNumberInstance(loc);
                                                    DecimalFormat df = (DecimalFormat)nf;
                                                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(loc);
                                                    symbols.setDecimalSeparator(',');
                                                    symbols.setGroupingSeparator('.');
                                                    df.setDecimalFormatSymbols(symbols);
                                                    df.applyPattern("###.#");
                                                    df.setRoundingMode(RoundingMode.DOWN);
                                                    System.out.println("===================S=====");
                                                    // System.out.println(
                                                    //         "Temperatura: " + df.format(((ReadTelemetryDataResp) response)
                                                    //                 .getTemperature()) + " °C");
                                                    System.out.println("Tensão: "
                                                            + df.format(((ReadTelemetryDataResp) response).getVoltage()) + " V");
                                                    System.out.println(
                                                            "Condutância: " + df.format(((ReadTelemetryDataResp) response)
                                                                    .getConductance()) + " S");
                                                    System.out.println("========================");
                                                } else {
                                                    System.out.println(
                                                            "Quantidade de bytes recebidos menor que o esperado.");
                                                }
                                            } else {
                                                System.out.println("Nenhuma resposta recebida.");
                                            }
                                        }
                                    } else {
                                        System.out.println("Não foi possível escrever na porta serial.");
                                    }

                                    System.out.println("Finalizado!");
                                } else {
                                    System.out.println(TEXTO_EXEMPLO_USO);
                                }
                            } else {
                                System.out.println(TEXTO_EXEMPLO_USO);
                            }
                        } else {
                            System.out.println(TEXTO_EXEMPLO_USO);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                } finally {
                    serial.freeResources();
                }
            } else {
                System.out.println(TEXTO_EXEMPLO_USO);
            }
        } else {
            System.out.println(TEXTO_EXEMPLO_USO);
        }
    }
}
