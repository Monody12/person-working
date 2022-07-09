package com.example.netdisk.controller;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.po.FileCnt;
import com.example.netdisk.entity.po.FileInfo;
import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.entity.vo.FileVo;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FileUploadService;
import com.example.netdisk.utils.SnowflakeIdWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author monody
 * @date 2022/4/27 12:18 上午
 */
@RequestMapping("/file")
@RestController
@Slf4j
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Base64 base64;

    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传文件", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件存放路径", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "file", value = "上传文件内容", dataType = "file", paramType = "fromData", required = true),
            @ApiImplicitParam(name = "md5", value = "上传文件md5值", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "size", value = "上传文件大小", dataType = "long", paramType = "query", required = false),
            @ApiImplicitParam(name = "detail", value = "文件描述信息", dataType = "String", paramType = "query", required = false)
    })
    public BaseResponseEntity upload(String username, String path, String md5, MultipartFile file, Long size, String detail) throws IOException {
        log.debug("MultipartFile: " + file);
        if (size == null) {
            size = 0L;
        }
        String fileId = fileUploadService.work(file, username, path, size, md5, detail);
        return BaseResponse.success(fileId);
    }

    @PostMapping(value = "/commonBatchUpload", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "批量普通上传文件", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件存放路径", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "files", value = "上传文件内容", dataType = "file", paramType = "fromData", allowMultiple = true, required = true),
            @ApiImplicitParam(name = "detail", value = "文件描述信息", dataType = "String", paramType = "query", required = false)
    })
    public BaseResponseEntity commonBatchUpload(String username, String path, @RequestParam(required = true, name = "file") MultipartFile[] files, String detail) throws IOException {
        // 记录上传成功的文件id
        List<String> successList = new ArrayList<>();
        // 用于记录上传失败的文件名称
        List<String> errorList = null;
        String fileId;
        for (MultipartFile multipartFile : files) {
            log.info("{} 用户上传文件 {}", username, multipartFile);
            try {
                fileId = fileUploadService.work(multipartFile, username, path, 0, null, detail);
                successList.add(fileId);
            } catch (Exception e) {
                if (errorList == null) {
                    errorList = new ArrayList<>();
                }
                String errorFileName = multipartFile.getOriginalFilename();
                errorList.add(errorFileName);
                // TODO 将错误信息记录到日志中进行分析
                log.error("{} 上传 {} 文件发生了异常, {}", username, errorFileName, e.getMessage());
            }
        }
        if (errorList != null) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("successList", successList);
            map.put("errorList", errorList);
            return BaseResponse.fail(500, "部分文件上传发生了错误！", map);
        }
        return BaseResponse.success(objectMapper.writeValueAsString(successList));
    }


    @GetMapping("/findByPath")
    @ApiOperation(value = "查询指定目录下的所有文件", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件存放路径", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "分页页数", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "size", value = "每页数量", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "field", value = "排序字段", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "order", value = "排序方式", dataType = "String", paramType = "query", required = false)
    })
    public BaseResponseEntity findByPath(String username, String path, int page, int size, String field, String order, @ApiIgnore HttpServletRequest request) {
        PageInfo<FileVo> filePageInfo = fileService.queryByPathVo(username, path, page, size, field, order);
        Map<String, Object> map = new HashMap<>(1);
        map.put("fileInfo", filePageInfo);
        return BaseResponse.success(map);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除一些文件", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", required = true),
            @ApiImplicitParam(name = "fileId", value = "要删除的文件id", allowMultiple = true, paramType = "query", required = true)
    })
    public BaseResponseEntity delete(String username, Long[] fileId) {
        if (fileId == null || fileId.length == 0) {
            log.debug("{} 要删除的文件id数组为 null", username);
            return BaseResponse.fail(username + "要删除的文件id数组为 null");
        }
        log.debug("{} 要删除的文件id为：{}", username, Arrays.toString(fileId));
        List<Long> deletedFileId = new ArrayList<>();
        for (long fileId1 : fileId) {

            try {
                int delete = fileService.delete(fileId1);
                if (delete == 1) {
                    deletedFileId.add(fileId1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                // TODO 将错误信息记录到日志中进行分析
            }
        }
        Map<String, Object> map = new HashMap<>(1);
        if (deletedFileId.size() != fileId.length) {
            map.put("deletedFileId", deletedFileId);
            return BaseResponse.fail(500, "部分文件未删除", map);
        } else {
            map.put("fileCnt", deletedFileId.size());
        }
        return BaseResponse.success(map);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新一个文件的名称或者描述", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "id", value = "文件id", dataType = "long", paramType = "query", required = true),
            @ApiImplicitParam(name = "detail", value = "文件描述信息", dataType = "String", paramType = "query", required = false),
            @ApiImplicitParam(name = "name", value = "文件名", dataType = "String", paramType = "query", required = false)
    })
    public BaseResponseEntity update(String username, long id, String detail, String name) {
        File file = new File();
        file.setUsername(username);
        file.setId(id);
        file.setDetail(detail);
        file.setName(name);
        int update = fileService.update(file);
        if (update == 0) {
            return BaseResponse.fail("文件信息未修改");
        }
        // 如果修改成功，则把修改好的文件信息返回给前端
        file = fileService.queryById(id);
        Map<String, Object> map = new HashMap<>(1);
        map.put("file", file);
        return BaseResponse.success(map);
    }

    @PostMapping("/move")
    @ApiOperation(value = "移动一些文件到指定的目录下", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "fileId", value = "文件id", dataType = "Long", paramType = "query", allowMultiple = true, required = true),
            @ApiImplicitParam(name = "path", value = "文件移动的目标路径", dataType = "String", paramType = "query", required = true)
    })
    public BaseResponseEntity move(String username, Long[] fileId, String path) {
        Map<String,Object> map = new HashMap<>(1);
        if (fileId == null || fileId.length == 0) {
            log.debug("{} 要移动的文件id数组为 {}",username,fileId );
            map.put("fileCnt","0");
            return BaseResponse.success(map);
        }
        // 将long数组转换为list
        List<Long> fileIdList = Arrays.asList(fileId);
        // TODO 校验该文件夹是否存在
        int i = fileService.batchUpdate(username, "path", path, "id", fileIdList);
        map.put("fileCnt",i);
        return BaseResponse.success(map);
    }

    @PostMapping("/copy")
    @ApiOperation(value = "复制一些文件到指定的目录下", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "fileId", value = "文件id", dataType = "Long", allowMultiple = true, paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件复制的目标路径", dataType = "String", paramType = "query", required = true)
    })
    public BaseResponseEntity copy(String username, Long[] fileId, String path) {
        // TODO 校验该文件夹是否存在
        List<Long> fileIdList = Arrays.asList(fileId);
        // 取出该文件的数据库文件信息
        List<File> files = fileService.queryByIds(username, fileIdList);
        if (files.size() != fileIdList.size()) {
            return BaseResponse.fail("权限错误");
        }
        // 取出该文件的扩展信息
        List<FileInfo> fileInfos = fileService.queryFileInfos(fileIdList);
        if (files.size() != fileInfos.size()) {
            return BaseResponse.fail("系统错误");
        }
        long id;
        // 该绝对路径文件引用次数
        Map<String, Integer> map = new TreeMap<>();
        // 排序，用于将两个List的fileId是对应的
        Collections.sort(fileIdList);
        Collections.sort(fileInfos);
        for (int i = 0; i < fileIdList.size(); i++) {
            // 获取当前文件信息的引用
            File file = files.get(i);
            FileInfo fileInfo = fileInfos.get(i);
            // 拷贝出的新文件的id
            id = snowflakeIdWorker.nextId();
            // 将新的id设定到新的文件信息中
            file.setId(id);
            fileInfo.setFileId(String.valueOf(id));
            // 将新的路径写入到新的文件信息中
            file.setPath(path);
            // 修改时间
            file.setUpdateTime(LocalDateTime.now());
            // 添加引用
            map.merge(fileInfo.getRealPath(), 1, Integer::sum);
        }
        // 将新增的信息批量写入 MySQL
        fileService.insertMany(files);
        // 将新增的信息批量写入 MongoDB
        mongoTemplate.insert(fileInfos, FileInfo.class);
        // 将旧的文件引用数量导出
        List<FileCnt> fileCnts = mongoTemplate.find(Query.query(Criteria.where("_id").in(map.keySet())), FileCnt.class);
        // 有序化
        Collections.sort(fileCnts);
        // 更新
        int index = 0;
        FileCnt fileCnt;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            fileCnt = fileCnts.get(index++);
            fileCnt.setNum(fileCnt.getNum() + entry.getValue());
        }
        // 将更新后的信息写入
        mongoTemplate.remove(Query.query(Criteria.where("_id").in(map.keySet())), FileCnt.class);
        mongoTemplate.insert(fileCnts, FileCnt.class);
        return BaseResponse.success("更新成功");
    }


}
