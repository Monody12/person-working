package com.example.netdisk.onlinedoc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "online_doc")
public class OnlineDoc {
    @Id
    String id;

    @Indexed
    String username;

    String title;

    String content;

    LocalDateTime createTime;

    LocalDateTime updateTime;

    public OnlineDoc() {
    }

    public OnlineDoc(String id,String username,String title) {
        this.id = id;
        this.username = username;
        this.title = title;
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

}
