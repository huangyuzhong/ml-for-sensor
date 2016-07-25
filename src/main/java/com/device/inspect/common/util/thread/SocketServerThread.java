package com.device.inspect.common.util.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.Application;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.restful.RestResponse;
import com.device.inspect.common.restful.device.RestInspectData;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.StringDate;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/23.
 */

public class SocketServerThread extends Thread {
    private Socket sock;
    PrintWriter out = null;
//    DataOutputStream dos = null;
    DataInputStream dins = null;
    public SocketServerThread(Socket s)
    {
        this.sock =s;
    }

    public void run(){
        try {

            InputStream ins = sock.getInputStream();
            dins = new DataInputStream(ins);
            //服务端解包过程
            boolean flag = false;
            while(!flag) {
                out = new PrintWriter(sock.getOutputStream());
                byte[] data = new byte[512];
                dins.read(data);
                String result = ByteAndHex.bytesToHexString(data);
                System.out.println(Thread.currentThread().getName()+"发来的内容是:" + result);
                String flagString = result.substring(2,4);
                String response = "";
                if (!result.startsWith("ef")){
                    flag = true;
                }else {
//                    Thread.sleep(1000);
                    response = get(result);
                }
//                response = get(result);
//                dos.writeUTF(response);
//                out.println(ByteAndHex.hexStringToBytes(response));
                byte[] bytes = ByteAndHex.hexStringToBytes(response);
                out.println(bytes);
                out.flush();

                dins.close();
                out.close();
                sock.close();
                flag = true;
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                dins.close();
                out.close();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String get(String message) throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod("http://localhost:8999/api/rest/socket/insert/data?result="+
        message);

        client.executeMethod(method);
        //打印服务器返回的状态
        System.out.println(method.getStatusLine());
        InputStream stream = method.getResponseBodyAsStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String line;
        while (null != (line = br.readLine())) {
            buf.append(line).append("\n");
        }
        System.out.println(buf.toString());
        String response = buf.toString();
        JSONObject jsonObject = JSON.parseObject(response);

        RestInspectData restInspectData = JSON.parseObject(String.valueOf(jsonObject.get("data")), RestInspectData.class);

        String result = "";
        result = "ef020500";
        int lowUp = (int)(restInspectData.getDeviceInspect().getLowUp()*1000);
        int lowDown = (int)(restInspectData.getDeviceInspect().getLowDown()*1000);
        int highUp = (int)(restInspectData.getDeviceInspect().getHighUp()*1000);
        int highDown = (int)(restInspectData.getDeviceInspect().getHighDown()*1000);

        result+=ByteAndHex.bytesToHexString(ByteAndHex.intToByteArray(lowUp));
        result+=ByteAndHex.bytesToHexString(ByteAndHex.intToByteArray(lowDown));
        result+=ByteAndHex.bytesToHexString(ByteAndHex.intToByteArray(highUp));
        result+=ByteAndHex.bytesToHexString(ByteAndHex.intToByteArray(highDown));

        result+="ff02";

        //释放连接
        method.releaseConnection();

        return result;
    }


}
