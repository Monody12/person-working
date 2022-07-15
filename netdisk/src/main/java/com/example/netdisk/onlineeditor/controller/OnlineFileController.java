package com.example.netdisk.onlineeditor.controller;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.onlineeditor.entity.OnlineDoc;
import com.example.netdisk.onlineeditor.service.OnlineFileService;
import com.example.netdisk.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName OnlineFileController
 * @Description TODO
 * @Author monody
 * @Date 2022/7/14 5:53 PM
 * Version 1.0
 */
@RestController
@RequestMapping("/onlinefile")
public class OnlineFileController {
    @Autowired
    FileService fileService;
    @Autowired
    OnlineFileService onlineFileService;
    @Value("${netdisk.upload.big-size}")
    long MAX_EDIT_FILE_SIZE;

    @GetMapping("/get")
    public BaseResponseEntity getEditFile(String id) {
        // 查询文件信息
        File file = fileService.queryById(Long.parseLong(id));
        if (file == null) {
            return BaseResponse.fail("文件不存在");
        } else if (file.getSize() > MAX_EDIT_FILE_SIZE) {
            return BaseResponse.fail("文件过大，不能编辑");
        }
        String content = onlineFileService.editFile(id);
        OnlineDoc onlineDoc = new OnlineDoc(id, file.getName(), content, file.getUpdateTime());
        Map<String, Object> map = new HashMap<>(1);
        map.put("onlinedoc", onlineDoc);
        return BaseResponse.success(map);
    }
}
