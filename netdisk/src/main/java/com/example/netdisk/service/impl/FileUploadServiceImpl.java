package com.example.netdisk.service.impl;

import com.example.netdisk.entity.po.FileCnt;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FileUploadService;
import com.example.netdisk.utils.FileUtil;
import com.example.netdisk.utils.SnowflakeIdWorker;
import com.example.netdisk.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author monody
 * @date 2022/4/27 4:15 下午
 */
@Service
@RefreshScope
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${netdisk.upload.big-size}")
    private long bigSize;
    @Value("${netdisk.upload.storage-root}")
    private String storageRoot;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public com.example.netdisk.entity.File resolveUpload(MultipartFile file) {
        // 解析文件信息
        com.example.netdisk.entity.File data = new com.example.netdisk.entity.File();
        data.setName(file.getOriginalFilename());
        data.setSize(file.getSize());
        data.setUpdateTime(LocalDateTime.now());
        data.setType(FileUtil.getType(data.getName()));
        return data;
    }

     @Override
    public void writeDatabase(com.example.netdisk.entity.File file) {
        fileService.insert(file);
    }

    @Override
    public void saveFileInfo(FileInfo fileInfo) {
        fileInfo.setType("upload");
        mongoTemplate.insert(fileInfo);
        Query query = Query.query(Criteria.where("_id").is(fileInfo.getRealPath()));
        Update update = new Update();
        update.inc("num");
        mongoTemplate.upsert(query, update, FileCnt.class);
    }

    @Override
    public FileInfo optimizeStorage(String md5, long size) {
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria.andOperator(Criteria.where("md5").is(md5), Criteria.where("size").is(size)));
        return mongoTemplate.findOne(query, FileInfo.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String work(MultipartFile multipartFile, String username, String path, long size, String md5, String detail) throws IOException {
        // TODO 是否要覆盖前端上传过来的size？
        // 生成文件id
        long fileId = snowflakeIdWorker.nextId();
        // 查看本机是否有该文件
        FileInfo fileInfo = optimizeStorage(md5, size);
        // 如果存在，则不需要优化, flag 为优化标记
        boolean flag = fileInfo != null;
        // 解析上传的文件信息
        com.example.netdisk.entity.File data = resolveUpload(multipartFile);
        // 保存文件信息至 MySQL
        data.setId(fileId);
        data.setDetail(detail);
        data.setUsername(username);
        data.setPath(path);
        writeDatabase(data);
        // 如果不需要优化，则进行磁盘写入
        if (!flag) {
            fileInfo = new FileInfo(String.valueOf(fileId), null, data.getSize(), null);
            // 磁盘写入
            String realPath = fileService.writeHardDisk(multipartFile,null,null);
            // 读取md5
            String md51 = FileUtil.getMD5(new File(storageRoot + realPath));
            // 读取文件大小
            size = multipartFile.getSize();
            // 前端传输的md5值不可信
            if (!md51.equals(md5)){
                // 使用新的md5值再次查询数据库中是否已包含了该文件
                FileInfo testFileInfo = fileUploadService.optimizeStorage(md51, size);
                // 发现了逻辑相同的文件
                if (testFileInfo!=null){
                    // 删除当前磁盘中的文件
                    fileService.deleteHardDisk(realPath);
                    // 替换当前文件的realPath
                    realPath = testFileInfo.getRealPath();
                }

            }
            // 将信息存入
            fileInfo.setMd5(md51);
            fileInfo.setRealPath(realPath);
        } else {
            fileInfo.setFileId(String.valueOf(fileId));
        }
        // 保存文件信息至 MongoDB
        saveFileInfo(fileInfo);
        return fileInfo.getFileId();
    }

}
