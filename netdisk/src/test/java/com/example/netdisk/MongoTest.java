package com.example.netdisk;

import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.onlinedoc.entity.OnlineDoc;
import com.example.netdisk.onlinedoc.service.OnlineDocService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author monody
 * @date 2022/4/27 5:14 下午
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class MongoTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void test2() {
        List<FileInfo>arrayList=new ArrayList<>();
        arrayList.add(new FileInfo("124","124",124L,"2022/4/30/abcd"));
        Collection<FileInfo> insert = mongoTemplate.insert(arrayList, FileInfo.class);
        System.out.println(insert);
    }

    @Test
    public void test1() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId("123");
        fileInfo.setRealPath("/root");
        fileInfo.setSize(1234L);
        FileInfo insert = mongoTemplate.insert(fileInfo);
        System.out.println(fileInfo);
        System.out.println(insert);
    }

    @Autowired
    OnlineDocService onlineDocService;

    @Test
    public void test3() {
        OnlineDoc onlineDoc = onlineDocService.createOnlineDoc("dev", "test2");
        System.out.println(onlineDoc);
    }

    /**
     * 测试查询用户所有的在线文档摘要
     */
    @Test
    public void test4() {
        List<OnlineDoc> onlineDocs = onlineDocService.getOnlineDocsList("dev");
        System.out.println(onlineDocs);
    }

    /**
     * 更新在线文档内容
     */
    @Test
    public void test5() {
        int i = onlineDocService.updateOnlineDocContent("28919058904645632", "Hello World");
        System.out.println(i);
    }
}
