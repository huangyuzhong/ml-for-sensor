package com.device.inspect.common.util.thread;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.StringDate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/23.
 */
public class SocketServerThread extends Thread {
    private Socket sock;

    private InspectDataRepository inspectDataRepository;

    private DeviceRepository deviceRepository;

    private InspectTypeRepository inspectTypeRepository;

    private DeviceInspectRepository deviceInspectRepository;

    public SocketServerThread(Socket s,InspectDataRepository inspectDataRepository,
                              DeviceRepository deviceRepository,InspectTypeRepository inspectTypeRepository,
                              DeviceInspectRepository deviceInspectRepository)
    {
        this.sock =s;
        this.inspectDataRepository = inspectDataRepository;
        this.deviceRepository = deviceRepository;
        this.inspectTypeRepository = inspectTypeRepository;
        this.deviceInspectRepository = deviceInspectRepository;
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
                if (!result.startsWith("ef")){
                    flag = true;
                }
                Date date = null;
                if (result.substring(2,3).equals("02")){
                    String monitorTypeCode = result.substring(4,5);
                    InspectType inspectType = inspectTypeRepository.findByCode(monitorTypeCode);
                    if (null != inspectType){
                        String year = result.substring(12,13);
                        String month = result.substring(14,15);
                        String day = result.substring(16,17);
                        String hour = result.substring(18,19);
                        String min = result.substring(20,21);
                        String sec = result.substring(22,23);
                        String stringDate = "20"+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
                        date = StringDate.stringToDate(stringDate,"yyyy-MM-dd HH:mm:ss");

                        String fisrtData = result.substring(26,33);
                        String secondData = result.substring(34,41);

                        Device device = deviceRepository.findOne(101);
                        

                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
        finally {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
