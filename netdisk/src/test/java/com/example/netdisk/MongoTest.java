package com.example.netdisk;

import com.example.netdisk.entity.po.FileInfo;
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
@SpringBootTest
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
}
