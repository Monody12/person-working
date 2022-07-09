package com.example.netdisk.service.impl;

import com.example.netdisk.entity.po.SharedFile;
import com.example.netdisk.service.ShareFileService;
import com.example.netdisk.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author monody
 * @date 2022/5/8 22:15
 */
@Service
@Slf4j
public class ShareFileServiceImpl implements ShareFileService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    ShareFileService shareFileService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Value("${netdisk.message.share-file-exchange}")
    String commonExchangeName;
    @Value("${netdisk.message.share-file-routing-key}")
    String routingKey;

    @Override
    public SharedFile add(String username, String code, List<Long> fileId, List<Long> folderId, int day) {
        // 计算过期时间
        LocalDateTime expireTime = null;
        if (day > 0) {
            // 减去一分钟，避免消息队列出现误差
            expireTime = LocalDateTime.now().plusDays(day).minusMinutes(1);
        }
        // 生成分享信息
        SharedFile sharedFile = new SharedFile(username, code != null ? code : UUIDUtil.getFour(),
                UUIDUtil.get(), fileId, folderId, expireTime);
        mongoTemplate.insert(sharedFile);
        // TODO 若有效期不为永久，则发送通知给消息队列到期删除
        return sharedFile;
    }

    @Override
    public List<SharedFile> searchAll(String username) {
        return mongoTemplate.find(Query.query(Criteria.where("username").is(username)), SharedFile.class);
    }

    @Override
    public SharedFile searchOne(String url) {
        return mongoTemplate.findOne(Query.query(Criteria.where("url").is(url)), SharedFile.class);
    }

    @Override
    public int delete(String username, List<String> url) {
        return (int) mongoTemplate.remove(Query.query(Criteria.where("url").in(url)), SharedFile.class).getDeletedCount();
    }


    /**
     * 发送自动删除过期分享文件信息消息
     *
     * @param url 文件的url
     * @param day
     */
    @Override
    public void sendMessage(String url, int day) {
        String key = routingKey + day;
        rabbitTemplate.convertAndSend(commonExchangeName, key, url);
    }

    /**
     * 自动删除过期的文件分享
     * 若发现没有过期，则不删除，3天后再进行检测
     *
     * @param message
     */
    @RabbitListener(queues = {"share_file_queue_dlx"})
    public void getMessage(Message message) {
        log.debug("监听到消息：{}", message.toString());
        String url = new String(message.getBody(), StandardCharsets.UTF_8);
        SharedFile sharedFile = searchOne(url);
        // 该分享信息被修改为了 永久
        if (sharedFile.getExpireTime() == null) {
            return;
        }
        // 该分享信息已过期，删除
        else if (sharedFile.getExpireTime().isBefore(LocalDateTime.now())) {
            mongoTemplate.remove(Query.query(Criteria.where("_id").is(url)), SharedFile.class);
            log.debug("{} 已被删除", url);
        }
        // 因为每次修改有效期，都会发一个信息到消息队列，所以这条信息就无效
        else {

        }

    }
}
