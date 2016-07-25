package com.device.inspect.common.util.thread;

import com.device.inspect.Application;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
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

/**
 * Created by Administrator on 2016/7/23.
 */

public class SocketServerThread extends Thread {
    private Socket sock;

    public SocketServerThread(Socket s)
    {
        this.sock =s;
    }

    public void run(){
        try {

            InputStream ins = sock.getInputStream();
            DataInputStream dins = new DataInputStream(ins);
            //服务端解包过程
            boolean flag = false;
            while(!flag) {
                byte[] data = new byte[512];
                dins.read(data);
                String result = ByteAndHex.bytesToHexString(data);
                System.out.println(Thread.currentThread().getName()+"发来的内容是:" + result);
                String flagString = result.substring(2,4);
                if (!result.startsWith("ef")){
                    flag = true;
                }else {
                    get(result);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void get(String message) throws IOException {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod("http://localhost:8999/api/rest/socket/insert/data?result="+
        "ef020112340016022014432305ffffd8f0ffffb1e0ef02");

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
        //释放连接
        method.releaseConnection();
    }


}
