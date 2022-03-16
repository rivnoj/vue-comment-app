package com.grtech.comm.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.grtech.comm.ISerialComm;
import com.grtech.util.ByteQueue;

import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.StopBits;

public class SerialComm implements ISerialComm {
	SerialPort port = null;
	private DataInputStream is;
	private DataOutputStream os;
	private boolean connected = false;
	private boolean connecting = false;
	private String instancia;

	public SerialComm(String instancia) {
		this.instancia = instancia;
		// System.out.println(getInstancia() + " - Instancia criada...");
	}

	public boolean connect(String comPort, int timeout) {
		this.connecting = true;
		this.connected = false;

		try {
			port = SerialPortBuilder.newBuilder(comPort)
					.setBaudRate(2400)
					.setParity(Parity.NONE)
					.setStopBits(StopBits.STOPBITS_1)
					.setFlowControl(FlowControl.NONE)
					.build();

			if (port != null) {
				port.setSerialPortTimeout(1000);
				
				if (!port.isClosed()) {
					is = new DataInputStream(port.getInputStream());
					os = new DataOutputStream(port.getOutputStream());
					this.connected = true;
					System.out.println("Porta serial " + comPort + " conectada!");
				} else {
					System.out.println("Porta serial " + comPort + " náo conectada.");
				}
			} else {
				System.out.println("Não foi possível abrir a porta serial " + comPort + ".");
			}
		} catch (IOException e) {
			e.printStackTrace();
			freeResources();
		} finally {
			this.connecting = false;
		}

		return this.connected;
	}

	public void freeResources() {
		// System.out.println(getInstancia() + " - liberarConexao()");
		this.connected = false;

		// if (is != null) {
		// 	try {
		// 		is.close();
		// 	} catch (IOException e) {
		// 		e.printStackTrace();
		// 	}
		// }

		// if (os != null) {
		// 	try {
		// 		os.close();
		// 	} catch (IOException e) {
		// 		e.printStackTrace();
		// 	}
		// }

		if (port != null) {
			try {
				port.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] read() {
		byte[] ab = null;

		if (this.connected) {
			try {
				int bytes_available = is.available();

				if (bytes_available > 0) {
					ab = new byte[bytes_available];
					is.read(ab);
				}
			} catch (IOException e) {
				e.printStackTrace();
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - read - nao esta conectado.");
			freeResources();
		}

		return ab;
	}

	public byte[] read(int qtd_byte) {
		byte[] ab = null;

		if (qtd_byte > 0) {
			if (this.connected) {
				try {
					int bytes_available = is.available();

					if (bytes_available >= qtd_byte) {
						ab = new byte[qtd_byte];
						is.read(ab);
					}
				} catch (IOException e) {
					e.printStackTrace();
					freeResources();
				}
			} else {
				System.out.println(getInstancia() + " - read - nao esta conectado.");
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - read - qtd bytes eh zero.");
		}

		return ab;
	}

	public void readByteAvailable(ByteQueue bq) {
		if (this.connected) {
			try {
				int bytes_available = is.available();

				if (bytes_available > 0) {
					bq.read(is, bytes_available);
				}
			} catch (IOException e) {
				e.printStackTrace();
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - readMsg(int) - nao esta conectado.");
			freeResources();
		}
	}

	public boolean write(byte[] ab) {
		boolean flag = false;

		if ((ab != null) && (ab.length > 0)) {
			if (this.connected) {
				try {
					os.write(ab);
					os.flush();
					flag = true;
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
					freeResources();
				}
			} else {
				System.out.println(getInstancia() + " - write - nao esta conectado.");
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - write - bq eh nulo.");
		}

		return flag;
	}

	public boolean writeByteAvailable(ByteQueue bq) {
		boolean flag = false;

		if ((bq != null) && (bq.size() > 0)) {
			if (this.connected) {
				try {
					while (is.available() > 0) {
						is.readByte();
					}

					bq.write(os);
					os.flush();
					flag = true;
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
					freeResources();
				}
			} else {
				System.out.println(getInstancia() + " - write - nao esta conectado.");
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - write - ab eh nulo.");
		}

		return flag;
	}

	/**
	 * @return Returns the connected.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @return Returns the instancia.
	 */
	public String getInstancia() {
		return instancia;
	}

	/**
	 * @param instancia The instancia to set.
	 */
	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}

	public boolean isDataAvailable() {
		boolean flag = false;

		try {
			if ((is != null) && (is.available() > 0)) {
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			freeResources();
		}

		return flag;
	}

	public void clearBuffer() {
		try {
			while (is.available() > 0) {
				is.readByte();
			}
		} catch (IOException e) {
			e.printStackTrace();
			freeResources();
		}
	}

	public boolean isConnecting() {
		return connecting;
	}

	public int bytesAvailable() {
		int aux = 0;

		if (this.connected) {
			try {
				aux = is.available();
			} catch (IOException e) {
				System.out.println(getInstancia() + " - bytesAvailable() - error: " + e.getMessage());
				e.printStackTrace();
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - bytesAvailable() - nao esta conectado.");
			freeResources();
		}

		return aux;
	}

	public void readByteAvailable(ByteQueue bq, int length) {
		if (isConnected()) {
			try {
				if (is.available() >= length) {
					bq.read(is, length);
				}
			} catch (IOException e) {
				e.printStackTrace();
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - readMsg(int) - nao esta conectado.");
			freeResources();
		}
	}

	public short readShort() {
		short aux = 0;

		if (isConnected()) {
			try {
				if (is.available() >= 2) {
					aux = is.readShort();
				}
			} catch (IOException e) {
				e.printStackTrace();
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - readShort() - nao esta conectado.");
			freeResources();
		}

		return aux;
	}

	public byte readByte() {
		byte aux = 0;

		if (isConnected()) {
			try {
				if (is.available() >= 1) {
					aux = is.readByte();
				}
			} catch (IOException e) {
				e.printStackTrace();
				freeResources();
			}
		} else {
			System.out.println(getInstancia() + " - readByte() - nao esta conectado.");
			freeResources();
		}

		return aux;
	}
}