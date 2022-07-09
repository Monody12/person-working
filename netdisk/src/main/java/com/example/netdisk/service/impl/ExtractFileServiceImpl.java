package com.example.netdisk.service.impl;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.service.ExtractFileService;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FileUploadService;
import com.example.netdisk.utils.FileUtil;
import com.example.netdisk.utils.SnowflakeIdWorker;
import com.example.netdisk.utils.UUIDUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * @author monody
 * @date 2022/5/4 18:35
 */
@Slf4j
@Service
public class ExtractFileServiceImpl implements ExtractFileService {
    @Autowired
    FileService fileService;
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    ExtractFileService extractFileService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;
    @Value("${netdisk.upload.storage-root}")
    private String storageRoot;

    @Override
    public ZipFile getZipFile(String fileId){
        String absolutePath = fileService.getAbsolutePath(fileId);
        return new ZipFile(absolutePath);
    }

    @Override
    public List<FileHeader> getFileHeaders(String fileId,String charset) throws ZipException {
        ZipFile zipFile = extractFileService.getZipFile(fileId);
        if (!zipFile.isValidZipFile()){
            throw new ZipException("压缩文件损坏！");
        }
        zipFile.setCharset(Charset.forName(charset));
        return zipFile.getFileHeaders();
    }

    @Override
    public List<FileHeader> getFileHeaders(String fileId,Charset charset) throws ZipException {
        ZipFile zipFile = extractFileService.getZipFile(fileId);
        zipFile.setCharset(charset);
        if (!zipFile.isValidZipFile()){
            throw new ZipException("压缩文件损坏！");
        }
        return zipFile.getFileHeaders();
    }

    @Override
    public long extractFile(String fileId, FileHeader fileHeader, String username, String filename,String path,String charset) throws ZipException {
        if (fileHeader.isEncrypted()){
            throw new RuntimeException("文件被加密");
        }else if (fileHeader.isDirectory()){
            throw new RuntimeException("无法直接解压文件夹");
        }
        // 生成解压出的文件信息
        long newFileId = snowflakeIdWorker.nextId();
        String realPath = fileService.generateRealPath();
        File file = new File(newFileId,username,filename,null,null, FileUtil.getType(filename),path,null);
        FileInfo fileInfo = new FileInfo(fileId,null,null,realPath);
        String uuid = fileService.getUuidFromRealPath(realPath);
        // 解压到磁盘
        ZipFile zipFile = extractFileService.getZipFile(fileId);
        zipFile.setCharset(Charset.forName(charset));
        // 文件解压缩的目录
        String dir = storageRoot+ fileInfo.getRealPath().substring(0,realPath.length()-uuid.length());
        zipFile.extractFile(fileHeader,dir,filename);
        // 生成解压出的文件信息
        String absolutePath = storageRoot + realPath;
        java.io.File newFile = new java.io.File(absolutePath);
        String md5 = FileUtil.getMD5(newFile);
        long size = newFile.length();
        // 分析解压出来的文件是否在系统中存在
        FileInfo searchFileInfo = fileUploadService.optimizeStorage(md5, size);
        // 系统中已经存在此文件
        boolean flag = searchFileInfo != null;
        if(flag){
            // 替换相对路径，加入md5 size属性
            fileInfo = searchFileInfo;
            // 新解压出来的文件可以删除了
            newFile.delete();
        }else {
            fileInfo.setSize(size);
            fileInfo.setMd5(md5);
        }
        fileInfo.setFileId(String.valueOf(newFileId));
        file.setSize(size);
        // 写入数据库
        fileUploadService.writeDatabase(file);
        fileUploadService.saveFileInfo(fileInfo);
        return newFileId;
    }
}
