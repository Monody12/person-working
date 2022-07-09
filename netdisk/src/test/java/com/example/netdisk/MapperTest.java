package com.example.netdisk;

import com.example.netdisk.entity.dto.BatchOperation;
import com.example.netdisk.mapper.FileMapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author monody
 * @date 2022/4/29 7:21 下午
 */
@SpringBootTest
@Slf4j
public class MapperTest {
    @Autowired
    FileMapper fileMapper;

    @Test
    public void test1(){
//        BatchOperation batchOperation = new BatchOperation("test","size",47446,"id",
//                List.of("2388250656243712","2465087528894464"));
//        int many = fileMapper.updateMany(batchOperation);
//        log.warn("{}",many);
    }
}
