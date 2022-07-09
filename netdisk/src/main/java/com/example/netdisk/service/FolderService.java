package com.example.netdisk.service;

import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.vo.FolderVo;
import com.github.pagehelper.PageInfo;

import java.io.IOException;
import java.util.List;

/**
 * @author monody
 * @date 2022/4/27 11:52 下午
 */
public interface FolderService {
    /**
     * 新建文件夹
     * @param username
     * @param name
     * @param detail
     * @param path
     * @return
     */
    int create(String username,String name,String detail,String path);

    /**
     * 查询一个用户指定路径下的文件夹
     * @param username
     * @param path
     * @param page
     * @param size
     * @return
     */
    PageInfo<Folder> queryByPath(String username,String path,int page,int size);

    PageInfo<FolderVo> queryByPathVo(String username, String path, int page, int size);

    List<FolderVo> queryByIdVo(String username,List<Long> folderIds);

    Folder queryById(String username,Long fileId);

    List<Folder> queryById(String username,List<Long> folderIds);

    /**
     * 模糊查询路径
     * 用于递归查找当前文件夹下的文件
     * @param username
     * @param path
     * @return
     */
    public abstract List<Folder> queryLikePath(String username, String path);

    /**
     * 该文件夹路径是否存在
     * 例如用户想要在 /doc 下创建一个a.txt 文件
     * 则需要查询 path = "/" && name = "doc" 文件夹是否存在
     * @param username
     * @param path
     * @param name
     * @return
     */
    boolean pathExist(String username,String path,String name);

    /**
     * 更新文件夹基本信息
     * @param username
     * @param folderId
     * @param name
     * @param detail
     * @return
     */
    int update(String username,long folderId,String name,String detail);


    int batchUpdate(String username, String setColumn, Object value, String whereColumn, List list);

    /**
     * 逐条批量更新文件夹信息
     * @param list
     * @return
     */
    int batchUpdate(List<Folder> list);

    /**
     * 复制到目标文件夹
     * @param username
     * @param folderIds
     * @param newPath
     * @return
     */
    int copy(String username,List<Long> folderIds,String newPath);

    /**
     * 批量从数据库中删除文件夹信息
     * @param username
     * @param folderIds
     * @return
     */
    int batchDelete(String username, List<Long> folderIds);

    /**
     * 递归删除文件夹
     * @param username
     * @param folderIds
     * @return
     */
    int[] realBatchDelete(String username, List<Long> folderIds) throws IOException;

    /**
     * 批量移动文件夹到回收站
     * @param username
     * @param folderIds
     * @return
     */
    int moveRecycleBin(String username,List<Long> folderIds);

    /**
     * 批量移出回收站
     * @param username
     * @param folderIds
     * @return
     */
    int removeRecycleBin(String username,List<Long> folderIds);

    /**
     * 文件夹移动位置 （内层文件夹和文件也一并移动）
     * @param username
     * @param folderIds
     * @param targetPath
     * @return
     */
    int[] move(String username, List<Long> folderIds, String targetPath);
}
