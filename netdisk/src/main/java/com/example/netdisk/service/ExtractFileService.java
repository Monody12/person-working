package com.example.netdisk.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author monody
 * @date 2022/5/4 18:14
 */
public interface ExtractFileService {

    /**
     * 获取压缩文件格式
     * @param fileId
     * @return
     */
    public ZipFile getZipFile(String fileId);

    /**
     * 获取一个压缩文件的文件信息
     * @param fileId 压缩文件id
     * @param charset 文件编码
     * @return 文件信息
     */
    List<FileHeader> getFileHeaders(String fileId,String charset) throws ZipException;

    /**
     * 获取一个压缩文件的文件信息
     * @param fileId 压缩文件id
     * @param charset 文件编码
     * @return 文件信息
     */
    List<FileHeader> getFileHeaders(String fileId, Charset charset) throws ZipException;

    /**
     * 解压出压缩包中的一个文件
     * @param fileId 压缩文件id
     * @param fileHeader
     * @param username
     * @param path
     * @param charset 文件编码
     * @return 解压出来的文件id
     */
    long extractFile(String fileId, FileHeader fileHeader, String username, String filename,String path,String charset) throws ZipException;

}
