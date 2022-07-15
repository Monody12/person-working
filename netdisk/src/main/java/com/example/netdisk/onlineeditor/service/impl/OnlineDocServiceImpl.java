package com.example.netdisk.onlineeditor.service.impl;

import com.example.netdisk.onlineeditor.entity.OnlineDoc;
import com.example.netdisk.onlineeditor.service.OnlineDocService;
import com.example.netdisk.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OnlineDocServiceImpl implements OnlineDocService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public OnlineDoc createOnlineDoc(String username, String title) {
        OnlineDoc onlineDoc = new OnlineDoc(String.valueOf(snowflakeIdWorker.nextId()), username, title);
        // 插入到mongodb中
        return mongoTemplate.save(onlineDoc);
    }

    @Override
    public int deleteOnlineDoc(String username, String id) {
        Query query = Query.query(Criteria.where("_id").is(id).and("username").is(username));
        return (int) mongoTemplate.remove(query, OnlineDoc.class).getDeletedCount();
    }

    @Override
    public int updateOnlineDocTitle(String id, String title) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("title", title).set("updateTime", LocalDateTime.now());
        return (int) mongoTemplate.updateFirst(query, update, OnlineDoc.class).getModifiedCount();
    }

    @Override
    public int updateOnlineDocContent( String id, String content) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("content", content).set("updateTime", LocalDateTime.now());
        return (int) mongoTemplate.updateFirst(query, update, OnlineDoc.class).getModifiedCount();
    }

    @Override
    public OnlineDoc getOnlineDoc(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), OnlineDoc.class);
    }

    @Override
    public List<OnlineDoc> getOnlineDocsList(String username) {
        Query query = Query.query(Criteria.where("username").is(username));
        query.fields().exclude("content");
        List<OnlineDoc> onlineDocs = mongoTemplate.find(query, OnlineDoc.class);
        // 按修改时间降序排序
        onlineDocs.sort((o1, o2) -> o2.getUpdateTime().compareTo(o1.getUpdateTime()));
        return onlineDocs;
    }

}
