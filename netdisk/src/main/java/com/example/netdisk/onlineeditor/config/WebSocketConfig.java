package com.example.netdisk.onlineeditor.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.ServletContext;

/**
 * @author monody
 * @date 2022/5/1 11:03 下午
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements ServletContextInitializer {
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }


    @Override
    public void onStartup(ServletContext servletContext) {
        // 允许 websocket 传输长文本 10万字节
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize","61000");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize","52428800");

    }
}
