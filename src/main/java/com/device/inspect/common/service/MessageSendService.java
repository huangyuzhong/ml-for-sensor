package com.device.inspect.common.service;

import com.device.inspect.common.model.charater.User;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Created by Administrator on 2016/10/29.
 */
public class MessageSendService {
    public static String push(User user){
        if (MessageSendService.shortMessage(user)){
            return "短信推送成功";
        }else if (MessageSendService.sendEmai(user)){
            return "邮箱推送成功";
        }else {
            return "推送失败";
        }
    }


    //阿里短信推送的appkey
     public static String appKey="23511383";
    //阿里短信推送的App Secret
     public static String appSecret="b9d6566fe254b76e94dbdfb99470c312";
     public static String url="http://gw.api.taobao.com/router/rest";
    //模板ID
    public static String ModelID="SMS_25085037";
    //短信推送
    public static boolean shortMessage(User user){
        if (user.getBindMobile()==1){
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
                request.setSmsFreeSignName("王康健");
                //短信模板变量(验证码)
                request.setSmsParam("");
                //手机号
                request.setRecNum(user.getTelephone());
                //短信模板ID
                request.setSmsTemplateCode(ModelID);
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


    // 发件人的 邮箱 和 密码
    public static String myEmailAccount = "intelab@ilabservice.com";
    public static String myEmailPassword = "Service@001";

    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般格式为: smtp.xxx.com
    // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
    public static String myEmailSMTPHost = "smtp.163.com";


    public static boolean  sendEmai(User user)  {
        if (user.getBindEmail()==1){
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
                MimeMessage message = createMimeMessage(session, myEmailAccount, user.getEmail(),user);

                // 4. 根据 Session 获取邮件传输对象
                Transport transport = session.getTransport();

                // 5. 使用 邮箱账号 和 密码 连接邮件服务器
                //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
                transport.connect(myEmailAccount, myEmailPassword);

                // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
                transport.sendMessage(message, message.getAllRecipients());

                // 7. 关闭连接
                transport.close();
                return true;
            }catch (Exception e){
                return false;
            }
        }else {
            return false;
        }

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
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,User user) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "Intelab云服务", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, user.getEmail(), "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject("设备报警", "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent("当前报警，请及时处理", "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
}
