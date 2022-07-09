package com.example.netdisk;

import com.example.netdisk.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author monody
 * @date 2022/5/3 09:19
 */
@SpringBootTest
public class ServiceTest {
    @Autowired
    FileService fileService;

    @Test
    public void test() throws IOException {
        String s = fileService.writeHardDisk(null, "f束带结发夫","2022/5/3/28e9528fef5f4644bc155e4d692270b8");
        System.out.println(s);
    }
}
