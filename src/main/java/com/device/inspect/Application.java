package com.device.inspect;

import com.alicom.mns.tools.DefaultAlicomMessagePuller;
import com.aliyuncs.exceptions.ClientException;
import com.device.inspect.common.ftp.FTPConfig;
import com.device.inspect.common.ftp.FTPStorageManager;
import com.device.inspect.common.influxdb.InfluxDBManager;
import com.device.inspect.common.service.FileUploadService;
import com.device.inspect.common.service.MessageReceiveService;
import com.device.inspect.common.setting.GeneralConfig;
import com.device.inspect.common.util.thread.AppShutdownHook;
import com.device.inspect.common.util.thread.IoTMessageWorker;
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
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Configuration
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableScheduling
@ImportResource("classpath:scheduler.xml")
public class Application {

    private static final int SOCKET_PORT = 8195;
    public static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static AzureConfig azureConfig;
    public static FTPConfig ftpConfig;
    public static FTPConfig offlineFTPConfig;
    public static GeneralConfig generalConfig;
    public static FileUploadService intelabStorageManager = null;
    public static FTPStorageManager offlineFTPStorageManager = null;
    public static InfluxDBManager influxDBManager = null;

    private static List<IoTMessageWorker> ioTMessageWorkers = null;
    private static final int IoTWorkerNumber = 4;

    private static  DefaultAlicomMessagePuller puller;


    public static void Stop(){
        for(IoTMessageWorker worker: ioTMessageWorkers){
            worker.Stop();
        }

        try{
            Thread.sleep(500);
            for(IoTMessageWorker worker: ioTMessageWorkers){
                if(worker.isAlive()){
                    LOGGER.warn(String.format("IoT message worker %s is still alive", worker.getName()));
                }
            }
            puller.stop();
        }catch (Exception ex){
            LOGGER.error("Exception in stopping intelab-wbe. Err:" + ex.getMessage());
        }
    }

    public static void main(String[] args) throws Throwable
    {
	    LOGGER.info("[NOTICE] backend start");
        loadAppConfig();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Runtime.getRuntime().addShutdownHook(new AppShutdownHook());

        startMessagePuller();
        startMQWorkers();
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

            if(generalConfig.getInfluxdb() != null){
                String influxDBServerIp = generalConfig.getInfluxdb().get("server_ip");
                if(influxDBServerIp != null){
                    String influxDBServerPort = generalConfig.getInfluxdb().get("port");

                    String configAuthenticationEnabled = generalConfig.getInfluxdb().get("authentication_enabled");

                    Boolean isAuth = false;
                    if(configAuthenticationEnabled != null){
                        isAuth = Boolean.parseBoolean(configAuthenticationEnabled);
                    }

                    String username = generalConfig.getInfluxdb().get("username");
                    String password = generalConfig.getInfluxdb().get("password");

                    if(isAuth){
                        assert(username != null);
                        assert (password != null);
                        LOGGER.info("Create influxdb manager with user " + username);
                        if(influxDBServerPort != null){
                            influxDBManager = new InfluxDBManager(influxDBServerIp, Integer.parseInt(influxDBServerPort), username, password);
                        }else{
                            influxDBManager = new InfluxDBManager(influxDBServerIp, username, password);
                        }
                    }else {
                       if (influxDBServerPort != null) {
                            influxDBManager = new InfluxDBManager(influxDBServerIp, Integer.parseInt(influxDBServerPort));
                        } else {
                            influxDBManager = new InfluxDBManager(influxDBServerIp);
                        }
                    }

                }
            }

            if(generalConfig.getRabbitmq() != null){
                String mqBrokerIp = generalConfig.getRabbitmq().get("broker_ip");
                assert (mqBrokerIp != null);

                String username = generalConfig.getRabbitmq().get("username");
                String password = generalConfig.getRabbitmq().get("password");
                String telemetry_queue = generalConfig.getRabbitmq().get("telemetry_queue_name");

                assert(username != null);
                assert(password != null);
                assert (telemetry_queue != null);

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
                LOGGER.info("Connection accept socket:" + socket);
                SocketServerThread thread = new SocketServerThread(socket);
                thread.start();

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static void startMQWorkers(){

        ioTMessageWorkers = new ArrayList<IoTMessageWorker>();

        for(int i=0; i<IoTWorkerNumber; i++){
            IoTMessageWorker worker = new IoTMessageWorker(i);

            ioTMessageWorkers.add(worker);


        }

        for(IoTMessageWorker worker: ioTMessageWorkers){
            worker.start();
        }

    }

    /**
     * 开启接收短信回复线程池
     */
    private static void startMessagePuller() {
        puller = new DefaultAlicomMessagePuller();

        //设置异步线程池大小及任务队列的大小，还有无数据线程休眠时间
        puller.setConsumeMinThreadSize(6);
        puller.setConsumeMaxThreadSize(16);
        puller.setThreadQueueSize(200);
        puller.setPullMsgThreadSize(1);
        //和服务端联调问题时开启,平时无需开启，消耗性能
        puller.openDebugLog(false);

        String accessKeyId="LTAIMmQjearxrjm0";
        String accessKeySecret="OgLonz3aVJSaerzJRjSTHPO5ufUxqY";

        String messageType="SmsUp";//此处应该替换成相应产品的消息类型
        String queueName="Alicom-Queue-1856701573729509-SmsUp";//在云通信页面开通相应业务消息后，就能在页面上获得对应的queueName,
        // 格式类似Alicom-Queue-xxxxxx-SmsReport
        try {
            puller.startReceiveMsg(accessKeyId,accessKeySecret, messageType, queueName, new MessageReceiveService.MyMessageListener());
            System.out.println("异步线程池已开启");
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
