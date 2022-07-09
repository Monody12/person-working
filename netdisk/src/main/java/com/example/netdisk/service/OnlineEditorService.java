package com.example.netdisk.service;

import java.io.IOException;

/**
 * @author monody
 * @date 2022/5/2 7:09 下午
 */
public interface OnlineEditorService {
    /**
     * 创建新的文件
     * @param username 用户名
     * @param path 文件路径
     * @param filename 文件名称
     * @return fileId
     */
    long createFile(String username, String path, String filename);

    /**
     * 从读取文本文件内容
     * @param fileId
     * @return 文件内容
     */
    String readFromDisk(String fileId) throws IOException;

    void saveToDisk(String fileId,String content) throws IOException;



}
