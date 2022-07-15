package com.example.netdisk.entity.po;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author monody
 * @date 2022/4/27 5:06 下午
 */
@Data
@Document
@CompoundIndex( def = "{'md5': 1, 'size': 1}")
public class FileInfo implements Serializable,Comparable<FileInfo> {
    @Id
    String fileId;
    /**
     * 文件类型
     * upload：上传到服务器的文件
     * edit：编辑后的文件
     */
    String type;
    String md5;
    Long size;
    String realPath;

    public FileInfo() {

    }

    public FileInfo(String fileId, String md5, Long size, String realPath) {
        this.fileId = fileId;
        this.md5 = md5;
        this.size = size;
        this.realPath = realPath;
    }

    /**
     * 全参构造函数
     */
    public FileInfo(String fileId, String type, String md5, Long size, String realPath) {
        this.fileId = fileId;
        this.type = type;
        this.md5 = md5;
        this.size = size;
        this.realPath = realPath;
    }

    @Override
    public int compareTo(FileInfo o) {
        return fileId.compareTo(o.fileId);
    }
}
