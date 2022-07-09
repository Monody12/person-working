package com.example.netdisk.controller;

import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.entity.vo.FolderVo;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FolderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author monody
 * @date 2022/4/27 12:18 上午
 */
@Slf4j
@RequestMapping("/folder")
@RestController
public class FolderController {
    @Autowired
    FolderService folderService;
    @Autowired
    FileService fileService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiOperation(value = "新建文件夹", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件所在的文件夹路径", dataType = "String", paramType = "query", required = true, example = "/"),
            @ApiImplicitParam(name = "name", value = "文件名", dataType = "String", paramType = "query", required = true, example = "music"),
            @ApiImplicitParam(name = "detail", value = "文件夹描述", dataType = "String", paramType = "query", required = false, example = "存放喜爱的音乐")
    })
    public BaseResponseEntity create(String username, String path, String name, String detail) {
        boolean exist = folderService.pathExist(username, path, name);
        if (exist) {
            return BaseResponse.fail("文件夹已存在");
        }
        int i = folderService.create(username, name, detail, path);
        return BaseResponse.success("文件夹创建成功");
    }

    @RequestMapping(value = "/queryByPath", method = RequestMethod.GET)
    @ApiOperation(value = "根据路径查询", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件所在的文件夹路径", dataType = "String", paramType = "query", required = true, example = "/music/"),
            @ApiImplicitParam(name = "page", value = "当前页数（最小为0）", dataType = "Integer", paramType = "query", required = false),
            @ApiImplicitParam(name = "size", value = "页面大小", dataType = "Integer", paramType = "query", required = false)
    })
    public BaseResponseEntity queryByPath(String username, String path, Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 999 : size;
        PageInfo<FolderVo> folderPageInfo = folderService.queryByPathVo(username, path, page, size);
        Map<String, Object> map = new HashMap<>(1);
        map.put("fileInfo", folderPageInfo);
        return BaseResponse.success(map);
    }

    @RequestMapping(value = "/queryByName", method = RequestMethod.GET)
    @ApiOperation(value = "查询一个文件夹下的文件夹", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "path", value = "文件所在的文件夹路径", dataType = "String", paramType = "query", required = true, example = "/"),
            @ApiImplicitParam(name = "name", value = "文件夹名称", dataType = "String", paramType = "query", required = true, example = "music"),
            @ApiImplicitParam(name = "page", value = "当前页数（最小）", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "size", value = "页面大小", dataType = "int", paramType = "query", required = true)
    })
    public BaseResponseEntity queryByName(String username, String path, String name, int page, int size) {
        boolean exist = folderService.pathExist(username, path, name);
        if (!exist) {
            return BaseResponse.fail("文件夹不存在");
        }
        PageInfo<FolderVo> folderPageInfo = folderService.queryByPathVo(username, path + "/" + name + "/", page, size);
        Map<String, Object> map = new HashMap<>(1);
        map.put("fileInfo", folderPageInfo);
        return BaseResponse.success(map);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "更新文件夹信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "folderId", value = "要修改的文件夹的id", dataType = "String", paramType = "query", required = true, example = "12345678910111213"),
            @ApiImplicitParam(name = "name", value = "修改成的文件名", dataType = "String", paramType = "query", required = false, example = "main.cpp"),
            @ApiImplicitParam(name = "detail", value = "文件夹备注", dataType = "String", paramType = "query", required = false, example = "编译原理实验一")
    })
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseEntity update(String username, long id, String name, String detail) {
        int update = folderService.update(username, id, name, detail);
        if (update == 0) {
            return BaseResponse.fail("文件夹更新失败");
        }
        return BaseResponse.success("文件夹更新成功");
    }

    @PostMapping("/move")
    @ApiOperation(value = "将一些文件夹移动到指定的目录下", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "Long", paramType = "query", allowMultiple = true, required = true),
            @ApiImplicitParam(name = "path", value = "要移动到的路径", dataType = "String", paramType = "query", required = true, example = "/download/")
    })
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseEntity move(String username, Long[] folderId, String path) {
        log.debug("username:{}, folderId:{}, path:{}", username, folderId, path);
        Map<String, Object> map = new HashMap<>(2);
        if (folderId == null || folderId.length == 0) {
            map.put("folderCnt", 0);
            map.put("fileCnt", 0);
            return BaseResponse.success(map);
        }
        List<Long> list = Arrays.stream(folderId).collect(Collectors.toList());
        int[] move = folderService.move(username, list, path);

        map.put("folderCnt", move[0]);
        map.put("fileCnt", move[1]);
        return BaseResponse.success(map);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除一些文件夹及其下面的文件夹和文件", notes = "递归删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "folderId", value = "文件夹id", dataType = "Long", paramType = "query", allowMultiple = true, required = true)
    })
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseEntity delete(String username, Long[] folderId) throws IOException {
        if (folderId == null || folderId.length == 0) {
            return BaseResponse.fail("请选择要删除的文件夹");
        }
        List<Long> list = Arrays.stream(folderId).collect(Collectors.toList());
        int[] batchDelete = folderService.realBatchDelete(username, list);
        if (batchDelete[0] == 0 && batchDelete[1] == 0) {
            return BaseResponse.fail("未删除任何文件夹和文件");
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("folderCnt", batchDelete[0]);
        map.put("fileCnt", batchDelete[1]);
        return BaseResponse.success(map);
    }


}
