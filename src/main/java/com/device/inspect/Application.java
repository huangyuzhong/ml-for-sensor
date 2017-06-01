package com.device.inspect;

import com.device.inspect.common.ftp.FTPConfig;
import com.device.inspect.common.ftp.FTPStorageManager;
import com.device.inspect.common.influxdb.InfluxDBManager;
import com.device.inspect.common.service.FileUploadService;
import com.device.inspect.common.setting.GeneralConfig;
import com.device.inspect.common.util.thread.SocketServerThread;
import com.device.inspect.common.azure.AzureConfig;
import com.device.inspect.common.azure.AzureStorageManager;
import com.device.inspect.common.util.CONST;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    public static FTPConfig ftpConfig;
    public static FTPConfig offlineFTPConfig;
    public static GeneralConfig generalConfig;
    public static FileUploadService intelabStorageManager = null;
    public static FTPStorageManager offlineFTPStorageManager = null;
    public static InfluxDBManager influxDBManager = null;

    public static void main(String[] args) throws Throwable
    {
	    LOGGER.info("[NOTICE] backend start");
        loadAppConfig();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        socketServerStart();
    }

    private static void loadAppConfig() throws Throwable{
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Map<String, String> env = System.getenv();
            String homePath = env.get("HOME");
            String intelabEnvironmentName = env.get("INTELAB_ENV");

//            homePath = homePath + "/WorkSpace/Project/InteLAB";
            String generalConfigFilePath = String.format("%s/intelab-configs/%s/config.yaml", homePath, intelabEnvironmentName);
            LOGGER.info("Loading Storage Config from File %s", generalConfigFilePath);
            generalConfig = mapper.readValue(new File(generalConfigFilePath), GeneralConfig.class);
            LOGGER.info(String.format("General Config: %s", ReflectionToStringBuilder.toString(generalConfig, ToStringStyle.MULTI_LINE_STYLE)));

            String storageType = generalConfig.getStorage().get("type");
            LOGGER.info("Storage Type " + storageType);

            if(storageType.equals("azure")){
                String configFilePath = String.format("%s/intelab-configs/%s/azure.yaml", homePath, intelabEnvironmentName);

                LOGGER.info(String.format("Loading Azure config from file %s", configFilePath));

                azureConfig = mapper.readValue(new File(configFilePath), AzureConfig.class);
                LOGGER.info(String.format("Loaded azure config -- %s",ReflectionToStringBuilder.toString(azureConfig,ToStringStyle.MULTI_LINE_STYLE)));

                intelabStorageManager = new AzureStorageManager(CONST.azureMediaStorageBlobContainerName, azureConfig.getStorage());
            }
            else if(storageType.equals("ftp")){
                String configFilePath = String.format("%s/intelab-configs/%s/ftp.yaml", homePath, intelabEnvironmentName);
                LOGGER.info(String.format("Loading ftp config from file %s", configFilePath));
                ftpConfig = mapper.readValue(new File(configFilePath), FTPConfig.class);
                LOGGER.info(String.format("Loaded ftp config -- %s",ReflectionToStringBuilder.toString(ftpConfig,ToStringStyle.MULTI_LINE_STYLE)));

                intelabStorageManager = new FTPStorageManager(ftpConfig.getFtp());
            }

            if(generalConfig.getInfluxdb() != null && generalConfig.getInfluxdb().get("enabled").equals("true")){
                String influxDBServerIp = generalConfig.getInfluxdb().get("server_ip");
                if(influxDBServerIp != null){
                    String influxDBServerPort = generalConfig.getInfluxdb().get("port");

                    if(influxDBServerPort != null){
                        influxDBManager = new InfluxDBManager(influxDBServerIp, Integer.parseInt(influxDBServerPort));
                    }else{
                        influxDBManager = new InfluxDBManager(influxDBServerIp);
                    }

                }
            }

            String offlineFTPConfigFilePath = String.format("%s/intelab-configs/%s/offlineFTP.yaml", homePath, intelabEnvironmentName);;
            File offlineFTPConfigFile = new File(offlineFTPConfigFilePath);
            if(offlineFTPConfigFile.exists() && !offlineFTPConfigFile.isDirectory()){
                LOGGER.info("Loading Offline FTP config from " + offlineFTPConfigFilePath);
                offlineFTPConfig = mapper.readValue(offlineFTPConfigFile, FTPConfig.class);
                LOGGER.info(String.format("Loaded offline ftp config -- %s", ReflectionToStringBuilder.toString(offlineFTPConfig,ToStringStyle.MULTI_LINE_STYLE)));

                offlineFTPStorageManager = new FTPStorageManager(offlineFTPConfig.getFtp());
            }
            else{
                LOGGER.info("Offline FTP config is not found. Pass");
            }
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
