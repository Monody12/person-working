package com.example.netdisk.onlineeditor.controller;

import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.onlineeditor.service.OnlineDocService;

import com.example.netdisk.utils.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        MyWebSocket.objectMapper = objectMapper;
    }

    static OnlineDocService onlineDocService;

    @Autowired
    public void setOnlineDocService(OnlineDocService onlineDocService) {
        MyWebSocket.onlineDocService = onlineDocService;
    }

    static MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        MyWebSocket.mongoTemplate = mongoTemplate;
    }

    static String EDIT_STORAGE_ROOT;

    @Value("${netdisk.edit.storage-root}")
    public void setEditStorageRoot(String editStorageRoot) {
        EDIT_STORAGE_ROOT = editStorageRoot;
    }

    // 保存所有在线socket连接
    private static Map<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();

    //当前连接（每个websocket连入都会创建一个MyWebSocket实例
    private Session session;

    //处理连接建立
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketMap.put(session.getId(), this);
        log.info("新的连接加入：{}", session.getId());
    }

    //接受消息
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到客户端{}消息：{}", session.getId(), message);
        try {
            // 将收到的字符串转为json格式
            Map map = objectMapper.readValue(message, Map.class);
            // 如果不含id和content字段，丢弃消息
            if (map == null || map.get("id") == null || map.get("content") == null) {
                this.sendMessage("消息格式不正确，丢弃消息！");
                return;
            }
            String id = map.get("id").toString(), content = map.get("content").toString();
            // 判断是否为编辑文件
            if ("file".equals(map.get("type"))) {
                // 在MongoDB中查询写入文件的路径
                FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), FileInfo.class);
                // 如果文件不存在，丢弃消息
                if (fileInfo == null) {
                    this.sendMessage("文件不存在，丢弃消息！");
                    return;
                }
                // 如果文件存在，更新文件内容
                FileUtil.writeFile(EDIT_STORAGE_ROOT + fileInfo.getRealPath(), content);
            } else {
                // 修改文档内容
                int i = onlineDocService.updateOnlineDocContent(id, content);
                if (i==0){
                    this.sendMessage("文档内容同步失败");
                    return;
                }
            }
            this.sendMessage("文档内容已同步");
        } catch (Exception e) {
            e.printStackTrace();
            this.sendMessage("服务器发生错误：" + e.getMessage());
        }

    }

    //处理错误
    @OnError
    public void onError(Throwable error, Session session) {
        log.info("发生错误{},{}", session.getId(), error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose() throws IOException {
        String sessionId = this.session.getId();

        log.info("连接关闭:{}", sessionId);

        // 移除服务器保存的websocket对象 以及 在线文件编辑内容
        webSocketMap.remove(sessionId);
    }

    //群发消息

    //发送消息
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    //广播消息
    public static void broadcast() {
        MyWebSocket.webSocketMap.forEach((k, v) -> {
            try {
                v.sendMessage("这是一条测试广播");
            } catch (Exception e) {
            }
        });
    }

}

