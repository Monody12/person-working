package com.example.netdisk;

import com.example.netdisk.utils.FileUtil;
import com.example.netdisk.utils.UUIDUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author monody
 * @date 2022/5/7 19:56
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String s = "#include <stdio.h>\n\nint main(){\n printf(\"Hello World!\");\n return 0;\n} ";
        // 测试将带换行符的字符串写入文件
        String filePath = "/Users/monody/Desktop/LICENSE";
        File file = new File(filePath);
        if (!file.exists()){
            file.createNewFile();
        }
        // 写入内容
//        FileWriter fileWriter = new FileWriter(file);
//        fileWriter.write(s);
//        fileWriter.close();
        // 读取文件内容
        String content = FileUtil.readFile(filePath);
        System.out.println(content);
    }
}
