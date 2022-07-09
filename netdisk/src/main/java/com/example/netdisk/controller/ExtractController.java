package com.example.netdisk.controller;

import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.service.ExtractFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author monody
 * @date 2022/5/4 21:21
 */
@Slf4j
@RestController
@RequestMapping("/compressed")
public class ExtractController {
    @Autowired
    ExtractFileService extractFileService;
    @Autowired
    ObjectMapper objectMapper;

    Map<String, Charset> charsets;

    {
        charsets = new TreeMap<>();
        charsets.put("GBK", Charset.forName("GBK"));
        charsets.put("CESU-8", Charset.forName("CESU-8"));
        charsets.put("UTF-8", Charset.forName("UTF-8"));
    }

    @GetMapping("/getInfo/{fileId}")
    @ApiOperation(value = "获取压缩文件中的信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "压缩文件id", dataType = "String", required = true),
            @ApiImplicitParam(name = "charset", value = "文件编码格式", dataType = "String", required = false, example = "GBK | CESU-8")
    })
    public BaseResponseEntity getInfo(@PathVariable String fileId, String charset,@ApiIgnore HttpServletResponse response) {
        String error;
        List<FileHeader> fileHeaders = null;
        // 正则校验，检测乱码
        String messyRegex = "[\\sa-zA-Z_0-9./\u4e00-\u9fa5 \\-!@#$%^&*+{};'<>?()（）？！￥\"\\\\]+";
        try {
            // 未设置编码，将进行自动检测
            if (charset == null) {
                for (Map.Entry<String, Charset> entry : charsets.entrySet()) {
                    fileHeaders = extractFileService.getFileHeaders(fileId, entry.getValue());
                    boolean flag = true;
                    for (FileHeader fileHeader : fileHeaders) {
                        if (!fileHeader.getFileName().matches(messyRegex)) {
                            log.debug("编码不正确：{}\n错误的字符串为：{}", entry.getKey(),fileHeader.getFileName());
                            flag = false;
                            break;
                        }
                    }
                    // 找到了可能正确的编码
                    if (flag) {
                        charset = entry.getKey();
                    }
                }
            } else {
                fileHeaders = extractFileService.getFileHeaders(fileId, charset);
            }
            String finalCharset = charset;
            String[] strings = fileHeaders.stream().map(i -> {
                byte[] bytes = i.getFileName().getBytes();
                CharsetEncoder charsetEncoder = Charset.forName(finalCharset).newEncoder();
                ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
                charsetEncoder.encode(CharBuffer.wrap(i.getFileName().toCharArray()),byteBuffer,false);
                return new String(byteBuffer.array());
//                return null;
            }).toArray(String[]::new);

            Arrays.stream(strings).forEach(i->log.debug("{}",i));
            Map<String, Object> map = new HashMap<>(2);
            map.put("charset", charset);
            map.put("fileInfo", strings);
            return BaseResponse.success(map);
        } catch (Exception e) {
            error = e.getMessage();
            log.debug("解压文件发生错误：{}", error);
        }
        return BaseResponse.fail(error);
    }

    @PostMapping("/getInfo")
    @ApiOperation(value = "将压缩文件解压到用户的网盘目录下", notes = "返回值：解压出来的文件id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true, example = "test"),
            @ApiImplicitParam(name = "fileHeader", value = "压缩文件信息", dataType = "String", paramType = "query", required = true,
                    example = "/"),
            @ApiImplicitParam(name = "fileId", value = "文件id", dataType = "String", paramType = "query", required = true, example = "music"),
            @ApiImplicitParam(name = "filename", value = "解压出文件的文件名", dataType = "String", paramType = "query", required = true, example = "frpc.exe"),
            @ApiImplicitParam(name = "path", value = "解压文件存放路径", dataType = "String", paramType = "query", required = true, example = "/我的在线解压/"),
            @ApiImplicitParam(name = "charset", value = "文件编码格式", dataType = "String", required = true, example = "GBK | CESU-8")
    })
    public BaseResponseEntity extract(String fileId, String fileHeader, String username, String filename, String path, String charset) {
        FileHeader fileHeader1;
        String error;
        try {
            fileHeader1 = objectMapper.readValue(fileHeader, FileHeader.class);
            long newFileId = extractFileService.extractFile(fileId, fileHeader1, username, filename, path, charset);
            Map<String, Object> map = new HashMap<>(1);
            map.put("newFileId", newFileId);
            return BaseResponse.success(map);
        } catch (Exception e) {
            error = e.getMessage();
            log.debug("解压文件发生错误：{}", error);
        }
        return BaseResponse.fail(error);
    }
}
