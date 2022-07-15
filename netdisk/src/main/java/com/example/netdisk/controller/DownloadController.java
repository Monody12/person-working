package com.example.netdisk.controller;

import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author monody
 * @date 2022/4/28 11:42 上午
 */
@Slf4j
@RestController
public class DownloadController {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    FileService fileService;
    @Value("${netdisk.upload.storage-root}")
    private String UPLOAD_STORAGE_ROOT;
    @Value("${netdisk.edit.storage-root}")
    private String EDIT_STORAGE_ROOT;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/download/{fileId}")
    private void fileChunkDownload(@PathVariable long fileId, Boolean isDownload,
                                   @ApiIgnore HttpServletRequest request,
                                   @ApiIgnore HttpServletResponse response) throws IOException {
        if (isDownload == null) {
            isDownload = false;
        }
        com.example.netdisk.entity.File downloadFile = fileService.queryById(fileId);
        // 注意：查询MongoDB主键一定要转为String
        FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(String.valueOf(fileId))), FileInfo.class);
        String error = null;
        if (fileInfo == null) {
            error = "文件不存在";
        }
        // 返回错误信息
        if (error != null) {
            response.setContentType("application/json;charset=utf-8");
            BaseResponseEntity responseEntity = BaseResponse.fail(error);
            response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
            return;
        }
        // 感谢 https://blog.csdn.net/qq_41389354/article/details/105043312
        // 下载文件的绝对路径
        String filePath;
        if ("upload".equals(fileInfo.getType())) {
            filePath = UPLOAD_STORAGE_ROOT + fileInfo.getRealPath();
        } else {
            filePath = EDIT_STORAGE_ROOT + fileInfo.getRealPath();
        }
        log.debug("文件下载的绝对路径为：{}", filePath);
        String range = request.getHeader("Range");
        log.info("current request rang:" + range);
        File file = new File(filePath);
        log.debug("文件是否存在：{}", file.exists());
        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length() - 1;
        log.info("文件开始位置：{}，文件结束位置：{}，文件总长度：{}", startByte, endByte, file.length());
        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                //判断range的类型
                if (ranges.length == 1) {
                    //类型一：bytes=-2343
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //类型二：bytes=2343-
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //类型三：bytes=22-2343
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
                log.error("Range Occur Error,Message:{}", e.getLocalizedMessage());
            }
        }
        //要下载的长度
        long contentLength = endByte - startByte + 1;
//        long contentLength = endByte - startByte;
        log.debug("要下载的长度:{}", contentLength);
        //文件名
        String fileName = downloadFile.getName();
        //文件类型
        String contentType = request.getServletContext().getMimeType(fileName);
//        解决下载文件时文件名乱码问题
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        fileName = new String(fileNameBytes, 0, fileNameBytes.length, StandardCharsets.ISO_8859_1);
        //各种响应头设置
        //支持断点续传，获取部分字节内容：
        response.setHeader("Accept-Ranges", "bytes");
        //http状态码要为206：表示获取部分内容
//        if (range!=null||isDownload==false&&("audio/mpeg".equals(contentType)||"video/mp4".equals(contentType))) {
        if (range != null || isDownload == false) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        response.setContentType(contentType);
//        response.setContentType("multipart/form-data");
        response.setHeader("Content-Type", contentType);
        //inline表示浏览器直接使用，attachment表示下载，fileName表示下载的文件名
        if (isDownload == null || !isDownload) {
            response.setHeader("Content-Disposition", "inline;filename=" + fileName);
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        }
        response.setHeader("Content-Length", String.valueOf(contentLength));
        // Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[8192];
            int len = 0;
            randomAccessFile.seek(startByte);
            //坑爹地方四：判断是否到了最后不足4096（buff的length）个byte这个逻辑（(transmitted + len) <= contentLength）要放前面！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            //不然会会先读取randomAccessFile，造成后面读取位置出错，找了一天才发现问题所在
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);

                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            outputStream.flush();
            outputStream.close();
            response.flushBuffer();
            randomAccessFile.close();

            log.info("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);
        } catch (ClientAbortException e) {
            log.warn("{}", e.getMessage());
            log.warn("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
            //捕获此异常表示拥护停止下载
        } catch (IOException e) {
            e.printStackTrace();
            log.error("用户下载IO异常，Message：{}", e.getLocalizedMessage());
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }///end try
    }

    //    @GetMapping("/download")
    private void fileChunkDownload(String filePath, HttpServletRequest request, HttpServletResponse response) {
        filePath = "/Users/monody/Downloads/yuanshen_setup_mihoyo_20210924233112.exe";
        String range = request.getHeader("Range");
        log.info("current request rang:" + range);
        File file = new File(filePath);
        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length() - 1;
        log.info("文件开始位置：{}，文件结束位置：{}，文件总长度：{}", startByte, endByte, file.length());

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                //判断range的类型
                if (ranges.length == 1) {
                    //类型一：bytes=-2343
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //类型二：bytes=2343-
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //类型三：bytes=22-2343
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }

            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = file.length() - 1;
                log.error("Range Occur Error,Message:{}", e.getLocalizedMessage());
            }
        }

        //要下载的长度
        long contentLength = endByte - startByte + 1;
        //文件名
        String fileName = file.getName();
        //文件类型
        String contentType = request.getServletContext().getMimeType(fileName);

        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        fileName = new String(fileNameBytes, 0, fileNameBytes.length, StandardCharsets.ISO_8859_1);

        //各种响应头设置
        //支持断点续传，获取部分字节内容：
        response.setHeader("Accept-Ranges", "bytes");
        //http状态码要为206：表示获取部分内容
//        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType(contentType);
        response.setHeader("Content-Type", contentType);
        //inline表示浏览器直接使用，attachment表示下载，fileName表示下载的文件名
        response.setHeader("Content-Disposition", "inline;filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(contentLength));
        // Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());

        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[8192];
            int len = 0;
            randomAccessFile.seek(startByte);
            //坑爹地方四：判断是否到了最后不足4096（buff的length）个byte这个逻辑（(transmitted + len) <= contentLength）要放前面！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
            //不然会会先读取randomAccessFile，造成后面读取位置出错，找了一天才发现问题所在
//            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
            while ((len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }

            outputStream.flush();
            response.flushBuffer();
            randomAccessFile.close();
            log.info("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);
        } catch (ClientAbortException e) {
            log.warn("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
            //捕获此异常表示拥护停止下载
        } catch (IOException e) {
            e.printStackTrace();
            log.error("用户下载IO异常，Message：{}", e.getLocalizedMessage());
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }///end try
    }

    //    @GetMapping("/download/{fileId}")
    public void download(@PathVariable long fileId, Boolean isDownload, String contentType, @ApiIgnore HttpServletResponse response) throws IOException {
        com.example.netdisk.entity.File downloadFile = fileService.queryById(fileId);
        // 注意：查询MongoDB主键一定要转为String
        FileInfo fileInfo = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(String.valueOf(fileId))), FileInfo.class);
        String error = null;
        if (fileInfo == null) {
            error = "文件不存在";
        }
        // 返回错误信息
        if (error != null) {
            response.setContentType("application/json;charset=utf-8");
            BaseResponseEntity responseEntity = BaseResponse.fail(error);
            response.getWriter().write(objectMapper.writeValueAsString(responseEntity));
            return;
        }
        // 下载文件的绝对路径
        String path = UPLOAD_STORAGE_ROOT + fileInfo.getRealPath();
        // 下载文件的文件名
        String name = downloadFile.getName();
        if (isDownload != null && isDownload) {
            response.setContentType("multipart/form-data");
            //设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
        } else if (contentType != null) {

            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType(contentType);
        }
        FileInputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            int len;
            byte[] buffer = new byte[10240];
            //通过response对象获取outputStream流
            out = response.getOutputStream();

            //将FileInputStream流写入到buffer缓冲区
            while ((len = in.read(buffer)) > 0) {
                //使用OutputStream将缓冲区的数据输出到浏览器
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
