package com.example.netdisk.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件夹
 * @author monody
 * @date 2022/4/26 11:39 下午
 */
@Data
public class FolderVo implements Serializable {
    private String id;
    private String username;
    private String name;
    private String detail;
    private String type;
    private String path;
    private LocalDateTime updateTime;
    private LocalDateTime expireTime;
    private Boolean logic;

    public FolderVo() {
    }

    public FolderVo(String id, String username, String name, String detail, String type, String path, LocalDateTime updateTime) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.detail = detail;
        this.type = type;
        this.path = path;
        this.updateTime = updateTime;
    }
}
