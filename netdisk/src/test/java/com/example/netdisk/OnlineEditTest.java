package com.example.netdisk;

import com.example.netdisk.onlineeditor.service.OnlineFileService;
import com.example.netdisk.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName OnlineEditTest
 * @Description TODO
 * @Author monody
 * @Date 2022/7/13 10:32 AM
 * Version 1.0
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class OnlineEditTest {

    @Autowired
    OnlineFileService onlineFileService;

    @Test
    public void test1(){
        String fileId = "29975666548015104";
        String content = onlineFileService.editFile(fileId);
        log.info("content: {}", content);
    }

    public static void main(String[] args) {
        // 读取文件内容能力测试
        String path = "/Users/monody/Desktop/LICENSE";
        String content = FileUtil.readFile(path);
        System.out.println(content);
    }

}
