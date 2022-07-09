package com.example.netdisk.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件
 * @author monody
 * @date 2022/4/26 11:38 下午
 */
@Data
public class FileVo implements Serializable,Comparable<FileVo> {
    private String id;
    private String username;
    private String name;
    private String detail;
    private Long size;
    private String type;
    private String path;

    private LocalDateTime updateTime;
    private LocalDateTime expireTime;
    private Boolean logic;

    public FileVo() {
    }

    public FileVo(String id, String username, String name, String detail, Long size, String type, String path, LocalDateTime updateTime) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.detail = detail;
        this.size = size;
        this.type = type;
        this.path = path;
        this.updateTime = updateTime;
    }

    /**
     * 按文件id排序 （保证排序后的数组是有序的）
     * @param o
     * @return
     */
    @Override
    public int compareTo(FileVo o) {
        return this.id.compareTo(o.id);
    }
}
