package com.device.inspect.common.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.device.inspect.Application;
import com.device.inspect.common.model.charater.User;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2016/10/29.
 */
public class MessageSendService {

    private static final Logger LOGGER = LogManager.getLogger(MessageSendService.class);

    /**
     * 推送报警信息
     * model短信模板ID/邮箱标题, message警报内容
     *
     * @param user
     * @param verify
     * @param message
     * @return
     */
    public static String pushAlertMessge(User user, String verify, String message) {
        if (MessageSendService.sendSms(user, verify, message, 1)) {
            return "短信推送成功";
        } else if (MessageSendService.sendEmaiToUser(user, verify, message, 1)) {
            return "邮箱推送成功";
        } else {
            return "推送失败";
        }
    }

    public static boolean pushAlertMsg(User usr, String message) {
        if (MessageSendService.sendSms(usr, "", message, 1)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean pushAlertMail(User usr, String message) {
        if (MessageSendService.sendEmaiToUser(usr, "", message, 1)) {
            return true;
        } else {
            return false;
        }
    }


    //阿里短信推送的appkey
    public static final String appKey = "23524999";
    //阿里短信推送的App Secret
    public static final String appSecret = "f37a86b1b0d2cef9670bf1c4ce1e23f2";
    //url
    public static final String url = "http://gw.api.taobao.com/router/rest";
    //验证码短信签名绑定手机号
    public static final String MessageName0 = "绑定手机号";
    //设备警报短信签名设备警报
    public static final String MessageName1 = "设备报警";
    //找回密码短信签名找回密码
    public static final String MessageName2 = "找回密码";
    //设备警报模板ID
    public static final String alertModelID = "SMS_25635204";
    //绑定手机号验证码模板ID
    public static final String verifyModelID = "SMS_25665210";
    //找回密码模板ID
    public static final String PasswordID = "SMS_25610360";

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIMmQjearxrjm0";
    static final String accessKeySecret = "OgLonz3aVJSaerzJRjSTHPO5ufUxqY";
    //验证码短信签名绑定手机号
    public static final String MessageSign1 = "INTELAB绑定手机";
    //设备警报短信签名设备警报
    public static final String MessageSign2 = "INTELAB设备报警";
    //找回密码短信签名找回密码
    public static final String MessageSign3 = "INTELAB找回密码";

    /**
     * 发送短信
     *
     * @param user        用户
     * @param verfyMobile 接收验证码的手机
     * @param message     信息内容
     * @param type        0是验证码  1是报警信息 2是发送密码
     * @return
     * @throws ClientException
     */
    public static boolean sendSms(User user, String verfyMobile, String message, Integer type) {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        if (type.equals(1)) {
            // 短信发送报警信息
            if (user.getBindMobile() != null && user.getBindMobile() == 1) {
                try {
                    //初始化acsClient,暂不支持region化
                    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                    IAcsClient acsClient = new DefaultAcsClient(profile);

                    //组装请求对象-具体描述见控制台-文档部分内容
                    SendSmsRequest request = new SendSmsRequest();
                    //必填:待发送手机号
                    request.setPhoneNumbers(user.getMobile());
                    //必填:短信签名-可在短信控制台中找到
                    request.setSignName(MessageSign2);
                    //必填:短信模板-可在短信控制台中找到
                    request.setTemplateCode("SMS_101150037");
                    //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                    request.setTemplateParam("{\"code\":\"" + message + "\"}");

                    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                    if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                        //请求成功
                        LOGGER.info(String.format("Successfully send SMS to %s. %s",
                                user.getTelephone(),
                                message));
                        return true;
                    } else {
                        LOGGER.warn(String.format("Failed to send SMS to %s. errCode %s, %s",
                                user.getTelephone(),
                                sendSmsResponse.getCode(),
                                sendSmsResponse.getMessage()));
                        return false;
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("Exception happened in sending SMS to cellphone of user %s. Err: %s",
                            user.getId(),
                            e.toString()));
                    return false;
                }
            } else {
                LOGGER.warn(String.format("User %s have not bind mobile, skip sending SMS", user.getId()));
                return false;
            }
        } else if (type.equals(0)) {
            // 短信发送验证码
            try {
                //初始化acsClient,暂不支持region化
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);

                //组装请求对象-具体描述见控制台-文档部分内容
                SendSmsRequest request = new SendSmsRequest();
                //必填:待发送手机号
                request.setPhoneNumbers(verfyMobile);
                //必填:短信签名-可在短信控制台中找到
                request.setSignName(MessageSign1);
                //必填:短信模板-可在短信控制台中找到
                request.setTemplateCode("SMS_101230023");
                //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                request.setTemplateParam("{\"code\":\"" + message + "\"}");

                //请求失败这里会抛ClientException异常
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                    //请求成功
                    LOGGER.info(String.format("Successfully send SMS to %s. %s",
                            verfyMobile,
                            message));
                    return true;
                } else {
                    LOGGER.warn(String.format("Failed to send SMS to %s. errCode %s, %s",
                            verfyMobile,
                            sendSmsResponse.getCode(),
                            sendSmsResponse.getMessage()));
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else if (type.equals(2)) {
            // 短信找回密码
            try {
                //初始化acsClient,暂不支持region化
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);

                //组装请求对象-具体描述见控制台-文档部分内容
                SendSmsRequest request = new SendSmsRequest();
                //必填:待发送手机号
                request.setPhoneNumbers(verfyMobile);
                //必填:短信签名-可在短信控制台中找到
                request.setSignName(MessageSign3);
                //必填:短信模板-可在短信控制台中找到
                request.setTemplateCode("SMS_101215025");
                //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                request.setTemplateParam("{\"code\":\"" + message + "\"}");

                //请求失败这里会抛ClientException异常
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                    //请求成功
                    LOGGER.info(String.format("Successfully send SMS to %s. %s",
                            verfyMobile,
                            message));
                    return true;
                } else {
                    LOGGER.warn(String.format("Failed to send SMS to %s. errCode %s, %s",
                            verfyMobile,
                            sendSmsResponse.getCode(),
                            sendSmsResponse.getMessage()));
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 发送短信
     * @param user    用户
     * @param message  信息内容
     * @param type  0是验证码  1是报警信息 2是发送密码
     * @return
     */
    public static boolean sendMessage(User user,String verfyMobile,String message,Integer type){
        if (type.equals(1)){
            //短信发送警报信息
            if (user.getBindMobile()!=null&&user.getBindMobile()==1){
                try {
                    String aliURL=url;
                    //appkey
                    String key=appKey;
                    //App Secret
                    String secret=appSecret;
                    TaobaoClient client=new DefaultTaobaoClient(aliURL,key,secret);
                    AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
                    //短信类型
                    request.setSmsType("normal");
                    //短信签名
                    request.setSmsFreeSignName(MessageName1);
                    //短信模板变量(验证码)
                    request.setSmsParamString("{code:"+"'"+message+"'"+"}");
                    //手机号
                    request.setRecNum(user.getMobile());
                    //调用短信报警推送模板
                    request.setSmsTemplateCode(MessageSendService.alertModelID);
                    AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(request);
                    //返回是否短信推送成功推送

                    if(!rsp.isSuccess()){
                        LOGGER.warn(String.format("Failed to send SMS to %s. errCode %s, %s",
                                user.getTelephone(),
                                rsp.getErrorCode(),
                                rsp.getResult().toString()));

                        return false;
                    }
                    else{
                        LOGGER.info(String.format("Successfully send SMS to %s. %s",
                                user.getTelephone(),
                                message));
                        return true;
                    }
                }catch (Exception e){
                    LOGGER.error(String.format("Exception happened in sending SMS to cellphone of user %s. Err: %s",
                            user.getId(),
                            e.toString()));
                    return false;
                }
            }else {
                LOGGER.warn(String.format("User %s have not bind mobile, skip sending SMS", user.getId()));
                return false;
            }
        }else if (type.equals(0)){
            //短信发送验证码
            try {
                String aliURL=url;
                //appkey
                String key=appKey;
                //App Secret
                String secret=appSecret;
                TaobaoClient client=new DefaultTaobaoClient(aliURL,key,secret);
                AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
                //短信类型
                request.setSmsType("normal");
                //短信签名
                request.setSmsFreeSignName(MessageName0);
                //短信模板变量(验证码)
                request.setSmsParamString("{code:"+"'"+message+"'"+"}");
                //手机号
                request.setRecNum(verfyMobile);
                //调用短信验证码模板
                request.setSmsTemplateCode(MessageSendService.verifyModelID);
                AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(request);
                //返回是否短信推送成功推送
                return rsp.isSuccess();
            }catch (Exception e){
                return false;
            }
        }else if (type.equals(2)){
            //短信找回密码
            try {
                String aliURL=url;
                //appkey
                String key=appKey;
                //App Secret
                String secret=appSecret;
                TaobaoClient client=new DefaultTaobaoClient(aliURL,key,secret);
                AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
                //短信类型
                request.setSmsType("normal");
                //短信签名
                request.setSmsFreeSignName(MessageName2);
                //短信模板变量(验证码)
                request.setSmsParamString("{code:"+"'"+message+"'"+"}");
                //手机号
                request.setRecNum(verfyMobile);
                //调用短信验证码模板
                request.setSmsTemplateCode(MessageSendService.PasswordID);
                AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(request);
                //返回是否短信推送成功推送
                return rsp.isSuccess();
            }catch (Exception e){
                return false;
            }
        }else {
            return false;
        }
    }

    // 发件人的 邮箱 和 密码"intelab@ilabservice.com"     "Service@001"
    public static String myEmailAccount = "intelab@ilabservice.com" ;
    public static String myEmailPassword = "Service@001";

    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般格式为: smtp.xxx.com
    // 钉邮的smtp服务器地址smtp.mxhichina.com.
    public static String myEmailSMTPHost = "smtp.mxhichina.com";

    //邮件标题
    public static final String EmailSubject ="设备警报";
    public static final String EmailVerify ="验证码";
    public static final String EmailPassword ="找回密码";

    // 测试账号的 邮箱账号"test@ilabservice.com"
    // LAB-164, 所有报警email都抄送到 这个地址， 以备debug
    public static String intelabTestEmailAccount = "test@ilabservice.com" ;


    /**
     * 推送邮件给指定邮箱
     * @param senderMail
     * @param receiverMail
     * @param subject
     * @param content
     * @return
     */
    public static boolean sendEmail(String senderMail, String receiverMail, String subject, String content){
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();                    // 参数配置
            props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.host", myEmailSMTPHost);        // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.auth", "true");            // 请求认证，参数名称与具体实现有关

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getDefaultInstance(props);
            session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

            // 3. 创建一封邮件
            MimeMessage mimeMessage = createMimeMessage(session, senderMail, receiverMail, subject, content);

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器
            //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
            transport.connect(myEmailAccount, myEmailPassword);

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

            // 7. 关闭连接
            transport.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 推送邮件给用户， 三类邮件， 报警， 验证码， 找回密码
     * @param user   用户
     * @param content  邮件内容
     * @param type  0是验证码，1是报警信息
     * @param verifyEmail 接收验证码的邮箱
     * @return
     */
    public static boolean sendEmaiToUser(User user, String verifyEmail, String content, Integer type)  {
        if (type.equals(1)){
            //邮件报警
            if (user.getBindEmail()!=null&&user.getBindEmail()==1){

                if(sendEmail(myEmailAccount, user.getEmail(), MessageSendService.EmailSubject, content)){
                    LOGGER.info(String.format("Successfully sent alert email to %s. %s", user.getEmail(), content));
                    return true;
                }else{
                    LOGGER.warn(String.format("Failed to send alert email to %s. %s", user.getEmail(), content));
                    return false;
                }


            }else {
                LOGGER.warn(String.format("User %s set void email, so skip sending email",
                        user.getId()));
                return false;
            }
        }else if (type.equals(0)){
            //发送验证码
            if(sendEmail(myEmailAccount, verifyEmail, MessageSendService.EmailVerify, content)){
                LOGGER.info(String.format("Successfully sent e-verify email to %s. %s", user.getEmail(), content));
                return true;
            }else{
                LOGGER.warn(String.format("Failed to send e-verify email to %s. %s", user.getEmail(), content));
                return false;
            }


        }else if (type.equals(2)){
            //找回密码
            if(sendEmail(myEmailAccount, verifyEmail, MessageSendService.EmailPassword, content)){
                LOGGER.info(String.format("Successfully sent retrieve-password email to %s. %s", user.getEmail(), content));
                return true;
            }else{
                LOGGER.warn(String.format("Failed to send retrieve-password email to %s. %s", user.getEmail(), content));
                return false;
            }

        }else {
            LOGGER.warn("Do not send email for unknown email type " + type);
            return false;
        }

    }

    /**
     * 推送邮件
     * @param content  邮件内容
     * @return
     */
    public static boolean sendEmailToIntelabTest(String content){
        try {

            if(Application.generalConfig.getEmail() == null || !Application.generalConfig.getEmail().get("enabled").equals("true")){
                LOGGER.info("Email functionality is disabled, skip sending email");
                return false;
            }

            // 创建邮件title
            Map<String, String> env = System.getenv();
            String intelabEnvironmentName = env.get("INTELAB_ENV");
            String onlyEmaliSubject = MessageSendService.EmailSubject +"--["+intelabEnvironmentName+"]环境";

            return sendEmail(myEmailAccount, intelabTestEmailAccount, onlyEmaliSubject, content);
        }catch (Exception e){
            LOGGER.error(String.format("Exception happened in constructing email subject using environment variable INTELAB_ENV"));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 推送短信
     * @param content  短信内容
     * @return
     */
    public static boolean sendMessageToManager(String content, String receiverNum){
        try {
            String at_cmgf = "at+cmgf=0";  // 指定机器用中文发送短信

            // 以下是把发送内容转化为报文code BEGIN
            String code = "0011000d91";  // 报文的前缀
            code+=NumTrans(receiverNum);
            code+="0008a0";  // "0008"表示PDU编码表，"ao"表示短信在服务器存放时间
            String contentUni = string2Unicode(content);
            int length = contentUni.length();
            int charNum = length/2;

            String str_m = Integer.toHexString(charNum);
            String str ="00";
            str_m=str.substring(0, 2-str_m.length())+str_m;

            code+=str_m+contentUni;
            // END

            String at_cmgs = "at+cmgs="+(code.length()/2-1);  // 指定后面发送的报文的长度，19=code.length/2-1。

            WriteSerialPort.write(at_cmgf, at_cmgs, code);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void sendMessageToInteLabManager (String str, String receiverNum){
        if (str.length() <= 70){
            sendMessageToManager(str, receiverNum);
        }else {
            int strCount = 1;
            int strSum = (str.length()%65 == 0)?str.length()/65 : (str.length()/65+1);
            while(str.length()/65 != 0){
                String subStr = str.substring(0, 65);
                str = str.substring(65);
                sendMessageToManager("("+strCount+"/"+strSum+")"+subStr, receiverNum);
                strCount++;
            }
            sendMessageToManager("("+strCount+"/"+strSum+")"+str, receiverNum);
        }
    }

    // 将收件人号码按报文要求进行转化
    private static String NumTrans(String receiverNum) {
        receiverNum = "86"+receiverNum+"f";
        StringBuffer sb = new StringBuffer(receiverNum);
        for (int i=1; i<sb.length(); i=i+2){
            char ch = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i-1));
            sb.setCharAt(i-1, ch);
        }
        return sb.toString();
    }

    // 中文转化为Unicode码
    private static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            String str_m = Integer.toHexString(c);
            String str ="0000";
            str_m=str.substring(0, 4-str_m.length())+str_m;
            // 转换为unicode
            unicode.append(str_m);
        }
        return unicode.toString();
    }


    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,
                                                String subject,String content) throws Exception {
        // 1. 创建一封邮件
        MimeMessage mimeMessage = new MimeMessage(session);

        // 2. From: 发件人
        mimeMessage.setFrom(new InternetAddress(sendMail, "Intelab云服务", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail,receiveMail, "UTF-8"));

        // 4. Subject: 邮件主题
        mimeMessage.setSubject(subject, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        mimeMessage.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        mimeMessage.setSentDate(new Date());

        // 7. 保存设置
        mimeMessage.saveChanges();

        return mimeMessage;
    }
}

