package com.example.netdisk.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件夹
 * @author monody
 * @date 2022/4/26 11:39 下午
 */
@Data
public class Folder implements Serializable {
    private Long id;
    private String username;
    private String name;
    private String detail;
    private String type;
    private String path;
    private LocalDateTime updateTime;
    private LocalDateTime expireTime;
    private Boolean logic;

    public Folder() {
    }

    public Folder(Long id, String username, String name, String detail, String path) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.detail = detail;
        this.path = path;
    }
}
