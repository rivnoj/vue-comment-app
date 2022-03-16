package com.grtech.comm;

import com.grtech.util.ByteQueue;

public interface ISerialComm 
{
	public void freeResources();
	public boolean connect(String com_port, int timeout);
	public boolean write(byte[] msg);
	public byte[] read();
	public void readByteAvailable(ByteQueue bq);
	public void readByteAvailable(ByteQueue bq, int length);
	public short readShort();
	public boolean writeByteAvailable(ByteQueue bq);
	public boolean isConnected();
	public boolean isConnecting();
	public int bytesAvailable();
	public byte readByte();
	public void clearBuffer();
	public boolean isDataAvailable();
}
