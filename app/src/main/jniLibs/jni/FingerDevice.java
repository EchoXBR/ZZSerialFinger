package com.zz.finger;

public class FingerDevice {

	static{
		//加载本地库
		try
		{
			System.loadLibrary("Finger_COM_Driver_CCB");
		}
		catch(UnsatisfiedLinkError e)
		{
			System.out.println(e.getMessage());
		}
	}

	public native static int JNIGetVersion(String portname, int baudrate,
										   int bufsize, byte[] devVersion, byte[] errMsg);
	public native static int JNIGetFeature(String portName, int baudrate,
										   int timeOut, byte[] tzData, byte[] errMsg);
	public native static int JNIGetTemplate(String portName, int baudrate,
											int timeOut, byte[] mbData, byte[] errMsg);

	public int GetFeature(String portName, int baudrate, int timeout,
						  byte[] tzData, byte[] errMsg) throws Exception{
		return JNIGetFeature(portName, baudrate, timeout, tzData, errMsg);
	}

	public int GetDevVersion(String portName, int baudrate, int bufSize,
							 byte[] devVersion, byte[] errMsg) throws Exception{
		return JNIGetVersion(portName, baudrate, bufSize, devVersion, errMsg);
	}

	public int GetTemplate(String portName, int baudrate, int timeout,
						   byte[] mbData, byte[] errMsg) throws Exception{
		return JNIGetTemplate(portName, baudrate, timeout, mbData, errMsg);
	}
}
