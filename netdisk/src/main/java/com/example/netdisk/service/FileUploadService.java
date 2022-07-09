package com.example.netdisk.service;

import com.example.netdisk.entity.po.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author monody
 * @date 2022/4/27 4:11 下午
 */
public interface FileUploadService {
    /**
     * 解析用户上传的文件
     * @param file 用户上传的文件信息
     */
    public abstract com.example.netdisk.entity.File resolveUpload(MultipartFile file) throws IOException;


    /**
     * 将这条文件信息写入关系型数据库 （例如MySQL）
     */
    public abstract void writeDatabase(com.example.netdisk.entity.File file);

    /**
     * 记录文件额外的信息 至 文档型数据库 （例如MongoDB）
     * 以便以后高速查询
     * @param fileInfo 文件额外信息
     */
    public abstract void saveFileInfo(FileInfo fileInfo);

    /**
     * 优化文件存储
     *
     * @param md5 当前文件的md5
     * @param size 当前文件的大小
     * @return 当前库中是否存在这个文件
     */
    public abstract FileInfo optimizeStorage(String md5, long size);

    public abstract String work(MultipartFile multipartFile,String username,String path,long size,String md5,String detail) throws IOException;
}
