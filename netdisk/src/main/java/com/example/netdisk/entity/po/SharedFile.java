package com.example.netdisk.entity.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author monody
 * @date 2022/5/5 18:01
 */
@Data
@Document
public class SharedFile {
    @Indexed
    private String username;
    private String code;
    @Indexed(unique = true)
    private String url;
    private List<Long> fileList;
    private List<Long> folderList;
    /**
     * 该分享过期时间
     */
    private LocalDateTime expireTime;

    public SharedFile() {
    }

    public SharedFile(String username, String code, String url, List<Long> fileList, List<Long> folderList, LocalDateTime expireTime) {
        this.username = username;
        this.code = code;
        this.url = url;
        this.fileList = fileList;
        this.folderList = folderList;
        this.expireTime = expireTime;
    }
}
