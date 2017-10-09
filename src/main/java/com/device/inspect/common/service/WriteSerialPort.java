package com.device.inspect.common.service;

import gnu.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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

    public static boolean sendEmailCommand(String smtpServerAddress, String fromEmailUsername, String fromEmailPassword, String rcptEmailNum, String contentUtf_8Code){
        if (portId != null) {
            String sendEmailCommand1 = "AT+EMAILCID=1" + "\n";
            String sendEmailCommand2 = "AT+EMAILTO=30" + "\n";
            String sendEmailCommand3 = "AT+SMTPCS=\"UTF-8\"" + "\n";
            String sendEmailCommand4 = "AT+SMTPSRV=\""+smtpServerAddress+"\",25" + "\n";
            String sendEmailCommand5 = "AT+SMTPAUTH=1,\"%s\",\"%s\"" + "\n";
            sendEmailCommand5 = String.format(sendEmailCommand5, fromEmailUsername, fromEmailPassword);
            String sendEmailCommand6 = "AT+SMTPFROM=\"%s\",\"Intelab模块\"" + "\n";
            sendEmailCommand6 = String.format(sendEmailCommand6, fromEmailUsername);
            String sendEmailCommand7 = "AT+SMTPRCPT=0,0,\"%s\",\"\"" + "\n";
            sendEmailCommand7 = String.format(sendEmailCommand7, rcptEmailNum);
            String sendEmailCommand8 = "AT+SMTPSUB=\"E8AEBEE5A487E8ADA6E68AA5\"" + "\n";  // 设置邮件的主题，我这里将它统一为：设备警报
            String sendEmailCommand9 = "AT+SMTPBODY=%d" + "\n";
            String sendEmailCommand10 = "AT+SMTPSEND" + "\n";

            try {
                openGPRS();

                String strb = "";
                int count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand1.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand2.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand3.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand4.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand5.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand6.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand7.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand8.getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                sendEmailCommand9 = String.format(sendEmailCommand9, contentUtf_8Code.length());
                while(!strb.contains("OK") || count == 1){
                    outputStream.write(sendEmailCommand9.getBytes());
                    Thread.sleep(500);
                    outputStream.write((contentUtf_8Code+"\n").getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                outputStream.write(sendEmailCommand10.getBytes());
                outputStream.flush();
                Thread.sleep(500);
                strb = new String(readFromPort(serialPort));

                Thread.sleep(500);
                closeGPRS();

                if (strb.contains("OK")){
                    return true;
                } else{
                    LOGGER.error("用SIM800发送邮件产生错误码为："+strb);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            LOGGER.error("Port is null, please checkout.");
        }
        return false;
    }

    public static String readOnSIM800(Integer index){
        try {
            String AT_CMGR = "AT+CMGR=%d\n";
            AT_CMGR = String.format(AT_CMGR, index);

            String strb = "";
            outputStream.write(AT_CMGR.getBytes());
            outputStream.flush();
            Thread.sleep(500);
            strb = new String(readFromPort(serialPort));

            if (strb.contains("OK")){
                strb = strb.split("\r\n")[2];
                strb = strb.substring(56);
                return strb;
            } else{
                return "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static Integer monitorOnSIM800(){
        if (serialPort != null){
            String strb = new String(readFromPort(serialPort));
            if (strb.contains("+CMTI: \"SM\",")){
                strb = strb.split("\r\n")[1];
                strb = strb.substring(12);
                return Integer.parseInt(strb);
            }
        } else{
            LOGGER.error("SerialPort is null, please checkout.");
        }
        return -1;
    }

    public static boolean write(String at_cmgf, String at_cmgs, String code) {
        if (portId != null){
            String strb = "";
            int count = 1;
            try {
                while(!strb.contains("OK") || count == 1){
                    outputStream.write((at_cmgf+"\n").getBytes());
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }

                strb = "";
                count = 1;
                while(strb.contains("ERROR") || count == 1) {
                    outputStream.write((at_cmgs + "\n").getBytes());
                    outputStream.write(code.getBytes());
                    outputStream.write(0x1A);
                    outputStream.flush();
                    Thread.sleep(500);
                    strb = new String(readFromPort(serialPort));
                    count++;
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            if (strb.contains("ERROR")){
                LOGGER.warn("Failed to send alert message by SIM800");
                return false;
            }else{
                LOGGER.info("Successfully send alert message by SIM800");
                return true;
            }
        } else{
            LOGGER.error("Port is null, please checkout.");
        }
        return false;
    }

    public static byte[] readFromPort(SerialPort serialPort){

        InputStream in = null;
        byte[] bytes = new byte[200];

        try {
            if(serialPort == null){
                return bytes;
            }
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

    public static void openGPRS() throws Exception{
        String AT_SAPBR1 = "AT+SAPBR=3,1,\"Contype\",\"GPRS\"" + "\n";
        String AT_SAPBR2 = "AT+SAPBR=3,1,\"APN\",\"CMNET\"" + "\n";
        String AT_SAPBR3 = "AT+SAPBR=1,1" + "\n";
        String AT_SAPBR4 = "AT+SAPBR=2,1" + "\n";

        String strb = "";
        int count = 1;
        while(!strb.contains("OK") || count == 1){
            outputStream.write(AT_SAPBR1.getBytes());
            outputStream.flush();
            Thread.sleep(500);
            strb = new String(readFromPort(serialPort));
            count++;
        }

        strb = "";
        count = 1;
        while(!strb.contains("OK") || count == 1){
            outputStream.write(AT_SAPBR2.getBytes());
            outputStream.flush();
            Thread.sleep(500);
            strb = new String(readFromPort(serialPort));
            count++;
        }

        strb = "";
        count = 1;
        while(!strb.contains("OK") || count == 1){
            outputStream.write(AT_SAPBR3.getBytes());
            outputStream.flush();
            Thread.sleep(500);
            strb = new String(readFromPort(serialPort));
            count++;
        }

        strb = "";
        count = 1;
        while(!strb.contains("OK") || count == 1){
            outputStream.write(AT_SAPBR4.getBytes());
            outputStream.flush();
            Thread.sleep(500);
            strb = new String(readFromPort(serialPort));
            count++;
        }
    }

    public static void closeGPRS() throws Exception{
        String AT_SAPBR5 = "AT+SAPBR=0,1" + "\n";

        String strb = "";
        int count = 1;
        while(!strb.contains("OK") || count == 1){
            outputStream.write(AT_SAPBR5.getBytes());
            outputStream.flush();
            strb = new String(readFromPort(serialPort));
            count++;
        }
    }

    public static void openPort(){
        try {
            LOGGER.info("Opening SIM800 serial port for message receiving");
            //打开串口COM3,设置超时时限为1000毫秒
            portId = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
            serialPort = (SerialPort) portId.open("WriteSerialPort", 1000);
            serialPort.setSerialPortParams(9600, // 波特率
                    SerialPort.DATABITS_8, // 数据位
                    SerialPort.STOPBITS_1, // 停止位
                    SerialPort.PARITY_NONE); // 校验位
            outputStream = serialPort.getOutputStream();
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    public static void closePort() {
        try {
            outputStream.close();
            serialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
