package com.example.netdisk.websocket;

import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.entity.req.FileContent;
import com.example.netdisk.service.OnlineEditorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author monody
 * @date 2022/5/1 10:08 下午
 */
@ServerEndpoint(value = "/websocket") //接受websocket请求路径
@Component  //注册到spring容器中
@Slf4j
public class MyWebSocket {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OnlineEditorService onlineEditorService;
    @Autowired
    MongoTemplate mongoTemplate;

    // 保存所有在线socket连接
//    private static Map<String,MyWebSocket> webSocketMap = new ConcurrentHashMap<>();
    // 保存在线的socket的文件信息
    private static Map<String, String> contentMap = new ConcurrentHashMap<>();

    //当前连接（每个websocket连入都会创建一个MyWebSocket实例
    private Session session;

    //处理连接建立
    @OnOpen
    public void onOpen(Session session){
        this.session=session;
//        webSocketMap.put(session.getId(),this);

        log.info("新的连接加入：{}",session.getId());
    }

    //接受消息
    @OnMessage
    public void onMessage(String message,Session session){
        log.info("收到客户端{}消息：{}",session.getId(),message);
        try{
            this.sendMessage("已收到消息！");
            contentMap.put(session.getId(),message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //处理错误
    @OnError
    public void onError(Throwable error,Session session){
        log.info("发生错误{},{}",session.getId(),error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose() throws IOException {
        String sessionId = this.session.getId();

        log.info("连接关闭:{}",sessionId);
        // 获取当前用户的fileId
        FileContent fileContent = objectMapper.readValue(contentMap.get(sessionId), FileContent.class);
        // 将该用户的信息保存到本地文件
        FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(fileContent.getFileId())), FileInfo.class);
        onlineEditorService.saveToDisk(fileContent.getFileId(),fileInfo.getRealPath());
        // 移除服务器保存的websocket对象
//        webSocketMap.remove(sessionId);
    }

    //群发消息

    //发送消息
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    //广播消息
    public static void broadcast(){
//        MyWebSocket.webSocketMap.forEach((k,v)->{
//            try{
//                v.sendMessage("这是一条测试广播");
//            }catch (Exception e){
//            }
//        });
    }

}

