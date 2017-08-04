package com.device.inspect.common.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fgz on 2017/7/10.
 */
public class WriteSerialPort {

    private static final Logger LOGGER = LogManager.getLogger(WriteSerialPort.class);

    private static CommPortIdentifier portId;
    private static SerialPort serialPort;
    private static OutputStream outputStream;

    public static void write(String at_cmgf, String at_cmgs, String code){
        try {
            //打开串口COM3,设置超时时限为1000毫秒
            portId = CommPortIdentifier.getPortIdentifier("COM3");
            serialPort = (SerialPort) portId.open("WriteSerialPort", 1000);
            serialPort.setSerialPortParams(9600, // 波特率
                    SerialPort.DATABITS_8, // 数据位
                    SerialPort.STOPBITS_1, // 停止位
                    SerialPort.PARITY_NONE); // 校验位
            outputStream = serialPort.getOutputStream();
            String strb = "";
            int count = 1;

            while(!strb.contains("OK") || count == 1){
                outputStream.write((at_cmgf+"\n").getBytes());
                outputStream.flush();
                strb = new String(readFromPort(serialPort));
                count++;
                Thread.sleep(3000);
            }

            strb = "";
            count = 1;
            while(strb.contains("ERROR") || count == 1) {
                outputStream.write((at_cmgs + "\n").getBytes());
                outputStream.write(code.getBytes());
                outputStream.write(0x1A);
                outputStream.flush();
                strb = new String(readFromPort(serialPort));
                count++;
                Thread.sleep(3000);
            }

            outputStream.close();
            serialPort.close();

            if (strb.contains("ERROR")){
                LOGGER.warn("Failed to send alert message by SIM800");
            }else{
                LOGGER.info("Successfully send alert message by SIM800");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFromPort(SerialPort serialPort){

        InputStream in = null;
        byte[] bytes = new byte[50];

        try {

            in = serialPort.getInputStream();
            while(in.available() > 0){
                in.read(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
        return bytes;
    }
}
