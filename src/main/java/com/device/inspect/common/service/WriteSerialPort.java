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

    private static String ate1 = "ATE1\n";
    private static String atv1 = "ATV1\n";
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
                outputStream.write(ate1.getBytes());
                outputStream.flush();
                strb = new String(readFromPort(serialPort));
                count++;
            }

            strb = "";
            count = 1;
            while(!strb.contains("OK") || count == 1){
                outputStream.write(atv1.getBytes());
                outputStream.flush();
                strb = new String(readFromPort(serialPort));
                count++;
            }

            strb = "";
            count = 1;
            while(!strb.contains("OK") || count == 1){
                outputStream.write((at_cmgf+"\n").getBytes());
                outputStream.flush();
                strb = new String(readFromPort(serialPort));
                count++;
            }

            outputStream.write((at_cmgs+"\n").getBytes());
            outputStream.write(code.getBytes());
            outputStream.write(0x1A);
            outputStream.flush();
            strb = new String(readFromPort(serialPort));

            outputStream.close();
            serialPort.close();

            if (strb.contains("ERROR")){
                LOGGER.info("短信发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("NO Such Port, Please Make sure the port is not invoked or exists");
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
