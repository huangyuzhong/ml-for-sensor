package com.device.inspect;

import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.InspectData;
import com.device.inspect.common.model.device.InspectType;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.DeviceRepository;
import com.device.inspect.common.repository.device.InspectDataRepository;
import com.device.inspect.common.repository.device.InspectTypeRepository;
import com.device.inspect.common.util.thread.SocketServerThread;
import com.device.inspect.common.util.transefer.ByteAndHex;
import com.device.inspect.common.util.transefer.StringDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;

@Configuration
@EnableAspectJAutoProxy
@SpringBootApplication
public class Application {

    private static final int SOCKET_PORT = 8192;

    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        socketServerStart();
    }

    private static void socketServerStart(){
        ServerSocket s = null;
        Socket socket = null;
//        BufferedReader br = null;
//        PrintWriter pw = null;
        try {
            //设定服务端的端口号
            s = new ServerSocket(SOCKET_PORT,50);
            while (true){
                //等待请求,此方法会一直阻塞,直到获得请求才往下走
                socket = s.accept();
                System.out.println("Connection accept socket:" + socket);
                SocketServerThread thread = new SocketServerThread(socket);
                thread.start();

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
