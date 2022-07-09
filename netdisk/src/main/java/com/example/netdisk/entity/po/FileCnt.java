package com.example.netdisk.entity.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author monody
 * @date 2022/4/28 9:04 下午
 */
@Data
@Document
public class FileCnt implements Comparable<FileCnt>{
    @Id
    String realPath;
    Long num;

    public FileCnt() {
    }

    public FileCnt(String realPath, Long num) {
        this.realPath = realPath;
        this.num = num;
    }

    @Override
    public int compareTo(FileCnt o) {
        return realPath.compareTo(o.realPath);
    }
}
