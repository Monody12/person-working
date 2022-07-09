package com.example.netdisk.entity.req;

import lombok.Data;

/**
 * 在线编辑文件使用类
 * @author monody
 * @date 2022/5/2 7:51 下午
 */
@Data
public class FileContent {
    /**
     * 该用户操作的文件id
     */
    private String fileId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 文件内容
     */
    private String content;
}
