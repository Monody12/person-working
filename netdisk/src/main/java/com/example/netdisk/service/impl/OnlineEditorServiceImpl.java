package com.example.netdisk.service.impl;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.OnlineEditorService;
import com.example.netdisk.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author monody
 * @date 2022/5/2 7:48 下午
 */
@Service  //注册到spring容器中
public class OnlineEditorServiceImpl implements OnlineEditorService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    FileService fileService;
    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public long createFile(String username, String path, String filename) {
        // 生成随机fileId
        long fileId =  snowflakeIdWorker.nextId();
        // 生成文件信息
        File file = new File(fileId, username, filename, null, 0L, "文档", path, null);
        // 生成文件扩展信息
        String realPath = fileService.generateRealPath();
        FileInfo fileInfo = new FileInfo(String.valueOf(fileId),null,0L,realPath);
        mongoTemplate.insert(fileInfo);
        fileService.insert(file);
        return fileId;
    }

    @Override
    public String readFromDisk(String fileId) throws IOException {
        FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)), FileInfo.class);
        String realPath = fileInfo.getRealPath();
        return fileService.readHardDisk(realPath);
    }

    @Override
    public void saveToDisk(String fileId, String content) throws IOException {
        FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)), FileInfo.class);
        String realPath = fileInfo.getRealPath();
        fileService.writeHardDisk(null,content,realPath);
    }



}
