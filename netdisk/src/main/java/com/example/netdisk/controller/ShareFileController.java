package com.example.netdisk.controller;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.po.SharedFile;
import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.entity.vo.FileVo;
import com.example.netdisk.entity.vo.FolderVo;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FolderService;
import com.example.netdisk.service.ShareFileService;
import com.example.netdisk.utils.UUIDUtil;
import com.example.redis.bean.RedisConfigBean;
import com.example.redis.template.RedisTemplateUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author monody
 * @date 2022/5/8 22:38
 */
@Slf4j
@RestController
@RequestMapping("/share")
@Import(RedisConfigBean.class)
public class ShareFileController {

    @Autowired
    ShareFileService shareFileService;
    @Autowired
    FileService fileService;
    @Autowired
    FolderService folderService;
    @Autowired
    RedisTemplateUtil redisTemplateUtil;


    static final String SHARE_URL_PREFIX = "share_url_";
    static final String SHARE_FILE_PREFIX = "share_file_";
    static final String SHARE_FOLDER_PREFIX = "share_folder_";

    private boolean dayCheck(int day) {
        return day < 0 || day == 1 || day == 3 || day == 7 || day == 30;
    }

    @PostMapping("/add")
    public BaseResponseEntity add(String username, String code, Long[] fileId, Long[] folderId, int day) {
        if (!dayCheck(day)) {
            return BaseResponse.fail("分享天数不合法！（负数表示永久，合法天数为：1、3、7、30）");
        }
        // 不允许文件和文件夹同时为空
        if (fileId == null && folderId == null) {
            return BaseResponse.fail("请选择要分享的文件或文件夹！");
        }
        if (fileId == null) {
            fileId = new Long[0];
        }
        if (folderId == null) {
            folderId = new Long[0];
        }
        List<Long> fileIdList = Arrays.asList(fileId);
        List<Long> folderIdList = Arrays.asList(folderId);
        SharedFile sharedFile = shareFileService.add(username, code, fileIdList, folderIdList, day);
        // 添加定时删除任务
        shareFileService.sendMessage(sharedFile.getUrl(), day);
        Map<String, Object> map = new HashMap<>(1);
        map.put("info", sharedFile);
        return BaseResponse.success(map);
    }

    @GetMapping("/searchAll")
    public BaseResponseEntity searchAll(String username) {
        List<SharedFile> sharedFiles = shareFileService.searchAll(username);
        Map<String, Object> map = new HashMap<>(1);
        map.put("info", sharedFiles);
        return BaseResponse.success(map);
    }

    /**
     * 用于用户本次访问分享链接的首次请求
     * 目的是鉴权，然后获取临时访问token
     *
     * @param url
     * @param code
     * @return
     */
    @PostMapping("/get/{url}")
    public BaseResponseEntity getUrl(@PathVariable String url, String code, Boolean newToken) {
        // newToken为true时，表示需要重新生成token
        if (newToken == null) {
            newToken = true;
        }
        // TODO 为了保护用户隐私，输出文件信息时应当将path路径给过滤掉
        SharedFile sharedFile = shareFileService.searchOne(url);
        if (sharedFile == null) {
            return BaseResponse.notFound("找不到该分享链接");
        }
        // 验证提取码
        if (sharedFile.getCode() != null) {
            if (code == null) {
                return BaseResponse.fail(401, "请输入提取码");
            } else if (!sharedFile.getCode().equals(code)) {
                log.debug("数据库中查询到的提取码：{} , 用户输入的提取码：{}", sharedFile.getCode(), code);
                return BaseResponse.fail(403, "提取码错误");
            }
        }
        // 查询出改分享链接下的文件和文件夹
        List<FolderVo> folderVoList = folderService.queryByIdVo(sharedFile.getUsername(), sharedFile.getFolderList());
        List<FileVo> fileVoList = fileService.queryByIdVo(sharedFile.getUsername(), sharedFile.getFileList());
        // 隐藏Path信息
        for (FolderVo folderVo : folderVoList) {
            folderVo.setPath(null);
        }
        for (FileVo fileVo : fileVoList) {
            fileVo.setPath(null);
        }
        Map<String, Object> map = new HashMap<>(4);
        if (newToken) {
            // 创建分享秘钥
            String token = UUIDUtil.get();
            // 存入redis
            redisTemplateUtil.insertString(SHARE_URL_PREFIX + token, url, 30L, TimeUnit.MINUTES);
            map.put("token", token);
        }
        map.put("info", sharedFile);
        map.put("folder", folderVoList);
        map.put("file", fileVoList);
        return BaseResponse.success(map);
    }

    /**
     * 获取要下载分享文件的临时下载id
     *
     * @param fileId         要下载的文件id
     * @param parentFolderId 要下载的文件的父文件夹id
     * @param token          提取分享文件秘钥
     */
    @PostMapping("/get/file")
    public BaseResponseEntity getFile(Long fileId, Long parentFolderId, String token,
                                      @ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) throws ServletException, IOException {
        log.debug("@PostMapping(\"/get/file\") : fileId = {}, parentFolderId = {}, token = {}", fileId, parentFolderId, token);
        // 从redis中查询出当前token对应的分享url
        String url = redisTemplateUtil.getString(SHARE_URL_PREFIX + token);
        if (url == null) {
            return BaseResponse.fail(401, "分享密钥已失效");
        }
        // 查询出该分享url对应的文件分享信息
        SharedFile sharedFile = shareFileService.searchOne(url);
        if (sharedFile == null) {
            return BaseResponse.notFound("共享链接已失效");
        }
        // 要下载的文件分享列表的根目录下
        if (parentFolderId == null) {
            // 能在列表中找到该文件，生成临时下载id
            if (sharedFile.getFileList().contains(fileId)) {
                String tempId = UUIDUtil.get();
                // 存入redis
                redisTemplateUtil.insertString(SHARE_FILE_PREFIX + tempId, String.valueOf(fileId), 30L, TimeUnit.MINUTES);
                return BaseResponse.success(tempId);
            }
        }
        // 要下载的文件在分享列表的某个目录下
        else {
            // 查询提交的这个目录是否在分享列表中
            if (sharedFile.getFolderList().contains(parentFolderId)) {
                // 查询该目录下是否有该文件
                Folder folder = folderService.queryById(sharedFile.getUsername(), parentFolderId);
                // 判断用户要下载的文件是否在该分享文件夹下
                File file = fileService.queryById(fileId);
                String parentFolderPath = folder.getPath();
                if (parentFolderPath.equals(file.getPath().substring(0, parentFolderPath.length()))) {
                    String tempId = UUIDUtil.get();
                    // 存入redis
                    redisTemplateUtil.insertString(SHARE_FILE_PREFIX + tempId, String.valueOf(fileId), 30L, TimeUnit.MINUTES);
                    return BaseResponse.success(tempId);
                }
            }

        }
        return BaseResponse.fail("未能找到符合条件的文件");
    }

    /**
     * 获取一个分享文件夹下的文件和文件夹信息
     *
     * @param sharedFile
     * @param folderId
     * @return
     */
    private Map<String, Object> getFolderContent(SharedFile sharedFile, Long folderId, boolean isRoot) {
        // 查询出该文件夹的信息
        Folder folder = folderService.queryById(sharedFile.getUsername(), folderId);
        // 查询出该文件夹下的文件夹和文件信息发给前端
        Map<String, Object> map = new HashMap<>(2);
        String folderPath = folder.getPath();
        // 如果查询不是 文件分享的根目录 ，则需要加上文件夹的名称
        if (!isRoot) {
            folderPath += folder.getName() + '/';
        }
        PageInfo<FolderVo> folderVoPageInfo = folderService.queryByPathVo(sharedFile.getUsername(), folderPath, 0, 999);
        PageInfo<FileVo> fileVoPageInfo = fileService.queryByPathVo(sharedFile.getUsername(), folderPath, 0, 999, null, null);
        // 过滤掉路径信息
        for (FolderVo folderVo : folderVoPageInfo.getList()) {
            folderVo.setPath(null);
        }
        for (FileVo fileVo : fileVoPageInfo.getList()) {
            fileVo.setPath(null);
        }
        map.put("folder", folderVoPageInfo);
        map.put("file", fileVoPageInfo);
        return map;
    }

    /**
     * 获取分享文件夹的列表信息
     *
     * @return
     */
    @GetMapping("/get/folder")
    public BaseResponseEntity getFolder(Long folderId, Long parentFolderId, String token) {
        log.debug("@GetMapping(\"/get/folder\"): folderId:{},parentFolderId:{},token:{}", folderId, parentFolderId, token);
        // 从redis中查询出当前token对应的分享url
        String url = redisTemplateUtil.getString(SHARE_URL_PREFIX + token);
        if (url == null) {
            return BaseResponse.fail(401, "分享密钥已失效");
        }
        // 查询出该分享url对应的文件分享信息
        SharedFile sharedFile = shareFileService.searchOne(url);
        if (sharedFile == null) {
            return BaseResponse.notFound("共享链接已失效");
        }
        // 要下载的文件分享列表的根目录下
        if (parentFolderId == null) {
            // 查询该目录下是否有该文件夹
            if (sharedFile.getFolderList().contains(folderId)) {
                Map<String, Object> map = getFolderContent(sharedFile, folderId, false);
                return BaseResponse.success(map);
            }
        } else {
            // 查询提交的这个目录是否在分享列表中
            if (sharedFile.getFolderList().contains(parentFolderId)) {
                // 验证父子关系
                Folder parentFolder = folderService.queryById(sharedFile.getUsername(), parentFolderId);
                if (parentFolder == null) {
                    return BaseResponse.fail(404, "父文件夹不存在");
                }
                Folder folder = folderService.queryById(sharedFile.getUsername(), folderId);
                if (folder == null) {
                    return BaseResponse.fail(404, "文件夹不存在");
                }
                String parentFolderPath = parentFolder.getPath();
                if (parentFolderPath.equals(folder.getPath().substring(0, parentFolderPath.length()))) {
                    Map<String, Object> map = getFolderContent(sharedFile, folderId, false);
                    return BaseResponse.success(map);
                }
            }
        }
        return BaseResponse.fail(404, "未能找到符合条件的文件夹");
    }


    @GetMapping("/download/{tempId}")
    public void download(@PathVariable String tempId, @ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) throws IOException, ServletException {

        // 从redis中查询出当前token对应的分享url
        String fileId = redisTemplateUtil.getString(SHARE_FILE_PREFIX + tempId);
        log.debug("将要下载的 fileId: {}", fileId);
        if (fileId == null) {
            return;
        }
        request.getRequestDispatcher("/download/" + fileId).forward(request, response);
    }


}
