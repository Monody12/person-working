package com.example.netdisk.onlinedoc.controller;

import com.example.netdisk.onlinedoc.service.OnlineDocService;
import com.example.netdisk.service.OnlineEditorService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
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

    // 保存所有在线socket连接
    private static Map<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();
    // 保存在线的socket的文件信息
    private static Map<String, String> contentMap = new ConcurrentHashMap<>();

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
        log.debug("检查组件：objectMapper = {} , onlineDocService = {}", objectMapper, onlineDocService);
        log.info("收到客户端{}消息：{}", session.getId(), message);
        try {
            contentMap.put(session.getId(), message);
            // 将收到的字符串转为json格式
            Map map = objectMapper.readValue(message, Map.class);
            // 如果不含id和content字段，丢弃消息
            if (map==null||map.get("id") == null || map.get("content") == null) {
                this.sendMessage("消息格式不正确，丢弃消息。");
                return;
            }
            String id = map.get("id").toString(), content = map.get("content").toString();
            // 修改文档内容
            int flag = onlineDocService.updateOnlineDocContent(id, content);
            if (flag == 1) {
                this.sendMessage("文档内容已同步");
            } else {
                this.sendMessage("文档内容同步失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.sendMessage("服务器发生错误："+e.getMessage());
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

        // 移除服务器保存的websocket对象
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

    public static void main(String[] args) throws JsonProcessingException {
//        String json = "{\"id\":\"1\",\"content\":\"这是一条测试广播\"}";
        String json = "{\"id\":\"28990974218731520\",\"content\":\"xczdxzcxz\"}";
        ObjectMapper objectMapper1 = new ObjectMapper();
        Map map = objectMapper1.readValue(json, Map.class);
        System.out.println(json);
        System.out.println(map);
    }
}

