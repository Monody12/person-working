package com.example.netdisk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author monody
 * @date 2022/5/3 20:24
 */
@SpringBootTest
public class Zip4jTest {
    @Test
    public void test(){

    }

//    public static void main(String[] args) {
//        String messyRegex = "[\\sa-zA-Z_0-9./\u4e00-\u9fa5 !@#$%^&*+{};'<>?()\"\\\\]+";
//        String a = "操作系统/a.txt";
//        System.out.println(a.matches(messyRegex));
//    }

    public static void main(String[] args) throws ZipException, JsonProcessingException {
        String messyRegex = "[\\sa-zA-Z_0-9./\u4e00-\u9fa5]+";
//        String messyRegex = "[\\sa-zA-Z_0-9./\u3400-\u4db5]+";
        Set<Map.Entry<String, Charset>> entries = Charset.availableCharsets().entrySet();
        String filePath = "/Users/monody/Desktop/归档.zip";
//        String filePath = "/Users/monody/Desktop/upload/2022/5/4/45ac73c79ef24aa7baa89c28b8f35eb1";
        ZipFile zipFile = new ZipFile(filePath);
        zipFile.setCharset(Charset.forName("CESU-8"));
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        fileHeaders.stream().map(i-> {
            try {
                return new String(i.getFileName().getBytes(StandardCharsets.UTF_8),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(System.out::println);
        zipFile.extractFile("会议字幕_秦伟勋的个人会议室_857918953.txt",
                "/Users/monody/Desktop/","会议字幕_秦伟勋的个人会议室_857918953.txt");
    }
}
