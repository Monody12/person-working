package com.example.netdisk.service.impl;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.dto.BatchOperation;
import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.vo.FolderVo;
import com.example.netdisk.mapper.FolderMapper;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FolderService;
import com.example.netdisk.utils.SnowflakeIdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author monody
 * @date 2022/4/27 11:52 下午
 */
@Service
@Slf4j
public class FolderServiceImpl implements FolderService {
    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    FolderMapper folderMapper;
    @Autowired
    FileService fileService;
    @Autowired
    FolderService folderService;

    @Override
    public int create(String username, String name, String detail, String path) {
        Folder folder = new Folder(snowflakeIdWorker.nextId(), username, name, detail, path);
        return folderMapper.insert(folder);
    }

    @Override
    public PageInfo<Folder> queryByPath(String username, String path, int page, int size) {
        PageHelper.startPage(page, size);
        List<Folder> folders = folderMapper.selectByPath(username, path);
        return new PageInfo<>(folders);
    }

    @Override
    public PageInfo<FolderVo> queryByPathVo(String username, String path, int page, int size) {
        PageHelper.startPage(page, size);
        List<FolderVo> folders = folderMapper.selectByPathVo(username, path);
        return new PageInfo<>(folders);
    }

    @Override
    public List<FolderVo> queryByIdVo(String username, List<Long> folderIds) {
        if (folderIds == null || folderIds.size() == 0) {
            return new ArrayList<>();
        }
        return folderMapper.selectManyVo(new BatchOperation(username, folderIds));
    }

    @Override
    public Folder queryById(String username, Long folderId) {

        return folderMapper.selectOne(username, folderId);
    }

    @Override
    public List<Folder> queryById(String username, List<Long> folderIds) {
        if (folderIds == null || folderIds.size() == 0) {
            return new ArrayList<>();
        }
        return folderMapper.selectMany(new BatchOperation(username, folderIds));
    }

    @Override
    public List<Folder> queryLikePath(String username, String path) {
        return folderMapper.selectLikePath(username, path);
    }

    @Override
    public boolean pathExist(String username, String path, String name) {
        return folderMapper.selectByName(username, path, name) != null;
    }

    @Override
    public int update(String username, long folderId, String name, String detail) {
        Folder folder = new Folder(folderId, username, name, detail, null);
        return folderMapper.update(folder);
    }

    @Override
    public int batchUpdate(String username, String setColumn, Object value, String whereColumn, List list) {
        BatchOperation batchOperation = new BatchOperation(username, setColumn, value, whereColumn, list);
        return folderMapper.updateMany(batchOperation);
    }

    @Override
    public int batchUpdate(List<Folder> list) {
        int cnt = 0;
        for (Folder folder : list) {
            cnt += folderMapper.update(folder);
        }
        return cnt;
    }

    @Override
    public int copy(String username, List<Long> folderIds, String newPath) {
        return 0;
    }

    @Override
    public int batchDelete(String username, List<Long> folderIds) {
        // 非空判断，否则动态SQL会抛出异常
        if (folderIds == null || folderIds.isEmpty()) {
            return 0;
        }
        return folderMapper.deleteMany(new BatchOperation(username, folderIds));
    }

    @Override
    public int[] realBatchDelete(String username, List<Long> folderIds) throws IOException {
        if (folderIds.isEmpty())
            return new int[2];
        int folderCnt = 0, fileCnt = 0;
        for (Long folderId : folderIds) {
            // 获取到当前文件夹的信息
            Folder folder = folderService.queryById(username, folderId);
            // 空文件夹
            if (folder == null) {
                continue;
            }
            String path = folder.getPath() + folder.getName() + '/';
            // 删除当前文件夹下的所有文件夹
            List<Folder> folders = folderService.queryLikePath(username, path);
            List<Long> folderIdList = folders.parallelStream().map(Folder::getId).collect(Collectors.toList());
            if (!folderIdList.isEmpty()) {
                folderCnt += folderService.batchDelete(username, folderIdList);
            }
            // 删除当前文件夹下的所有文件
            List<File> files = fileService.queryLikePath(username, path);
            for (File file : files) {
                fileService.delete(file.getId());
            }
            fileCnt += files.size();
        }
        // 将外层的文件夹也一并删除
        folderCnt += folderService.batchDelete(username, folderIds);
        return new int[]{folderCnt, fileCnt};
    }

    @Override
    public int moveRecycleBin(String username, List<Long> folderIds) {
        if (folderIds == null || folderIds.isEmpty()) {
            return 0;
        }
        return batchUpdate(username, "logic", 0, "id", folderIds);
    }

    @Override
    public int removeRecycleBin(String username, List<Long> folderIds) {
        if (folderIds == null || folderIds.isEmpty()) {
            return 0;
        }
        return batchUpdate(username, "logic", 1, "id", folderIds);
    }

    @Override
    public int[] move(String username, List<Long> folderIds, String targetPath) {
        // 将要被移动的文件夹信息
        List<Folder> moveFolders = folderService.queryById(username, folderIds);
        // 数量统计
        int folderCnt = 0, fileCnt = 0;
        // 依次找出每个文件夹下面的文件夹和文件
        for(Folder folder : moveFolders) {
            log.debug("移动文件夹：{}，路径：{}", folder.getName(), folder.getPath());
            int len = folder.getPath().length();
            // 当前文件夹的绝对路径
            String path = folder.getPath() + folder.getName() + '/';
            log.debug("当前文件夹的绝对路径：{}", path);
            // 找出当前文件夹下的文件夹
            List<Folder> folders = queryLikePath(username, path);
            log.debug("当前文件夹下的文件夹：{}", folders);
            // 找出当前文件夹下的文件
            List<File> files = fileService.queryLikePath(username, path);
            log.debug("当前文件夹下的文件：{}", files);
            // 将文件夹和文件的绝对路径修改
            for(Folder f : folders) {
                log.debug("folderName: {} targetPath: {}, path: {}", folder.getName(),targetPath, f.getPath().substring(len));
                f.setPath(targetPath+f.getPath().substring(len));
            }
            for(File f : files) {
                log.debug("fileName: {} targetPath: {}, path: {}", folder.getName(),targetPath, f.getPath().substring(len));
                f.setPath(targetPath+f.getPath().substring(len));
            }
            // 提交修改
            folderCnt += batchUpdate(folders);
            fileCnt += fileService.batchUpdate(files);
            // 修改当前文件夹
            folder.setPath(targetPath);
        }
        folderCnt += batchUpdate(moveFolders);
        return new int[]{folderCnt, fileCnt};
    }

}
