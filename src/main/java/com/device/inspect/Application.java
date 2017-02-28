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
import com.device.inspect.common.azure.AzureConfig;
import com.device.inspect.common.azure.AzureStorageManager;
import com.device.inspect.common.util.CONST;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Configuration
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableScheduling
public class Application {

    private static final int SOCKET_PORT = 8193;
    public static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static AzureConfig azureConfig;
    public static AzureStorageManager intelabStorageManager = null;

    public static void main(String[] args) throws Throwable
    {
        loadAppConfig();
        initializeAzureServices();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        socketServerStart();
    }

    private static void initializeAzureServices(){
        intelabStorageManager = new AzureStorageManager(CONST.azureMediaStorageBlobContainerName, azureConfig.getStorage());
    }

    private static void loadAppConfig() throws Throwable{
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Map<String, String> env = System.getenv();
            String homePath = env.get("HOME");
            String intelabEnvironmentName = env.get("INTELAB_ENV");

            String configFilePath = String.format("%s/intelab-configs/%s/azure.yaml", homePath, intelabEnvironmentName);

            LOGGER.info(String.format("Loading Azure config from file %s", configFilePath));

            azureConfig = mapper.readValue(new File(configFilePath), AzureConfig.class);
            LOGGER.info(String.format("Loaded azure config -- %s",ReflectionToStringBuilder.toString(azureConfig,ToStringStyle.MULTI_LINE_STYLE)));


        } catch (Exception e) {
           LOGGER.error(String.format("Failed to load configuration, %s", e.toString()));
           throw e;
        }
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
                LOGGER.info("Connection accept socket:" + socket);
                SocketServerThread thread = new SocketServerThread(socket);
                thread.start();

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
