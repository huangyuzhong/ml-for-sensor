//package com.device.inspect.common.service;
//
//import com.device.inspect.common.model.device.Device;
//import com.device.inspect.common.repository.device.DeviceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.OnClose;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.concurrent.CopyOnWriteArraySet;
//
///**
// * Created by Administrator on 2016/7/25.
// */
//@ServerEndpoint("/websocket")
//@Component
//public class WebSocketInspectData {
//
//    private static CopyOnWriteArraySet<WebSocketInspectData> webSocketSet = new CopyOnWriteArraySet<>();
//
//    private static int onlineCount = 0;
//
//    @OnOpen
//    public void onOpen (){
//
//        webSocketSet.add(this);
//        addOnlineCount();
//        System.out.println("有新链接加入!当前在线人数为" + getOnlineCount());
//    }
//
//    @OnClose
//    public void onClose (){
//        webSocketSet.remove(this);
//        subOnlineCount();
//        System.out.println("有一链接关闭!当前在线人数为" + getOnlineCount());
//    }
//
//    @OnMessage
//    public void onMessage (String message) throws IOException {
//        System.out.println("来自客户端的消息:" + message);
//
//        // 群发消息
////        for ( WebSocketInspectData item : webSocketSet ){
////            item.sendMessage(message);
////        }
//    }
//
////    public void sendMessage (String message) throws IOException {
////        this.session.getBasicRemote().sendText(message);
////    }
//
//    public static synchronized  int getOnlineCount (){
//        return WebSocketInspectData.onlineCount;
//    }
//
//    public static synchronized void addOnlineCount (){
//        WebSocketInspectData.onlineCount++;
//    }
//
//    public static synchronized void subOnlineCount (){
//        WebSocketInspectData.onlineCount--;
//    }
//}
