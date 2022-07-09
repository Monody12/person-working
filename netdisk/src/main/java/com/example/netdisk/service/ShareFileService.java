package com.example.netdisk.service;

import com.example.netdisk.entity.po.SharedFile;

import java.util.List;

/**
 * 用户分享自己的文件和文件夹
 * @author monody
 * @date 2022/5/5 18:10
 */
public interface ShareFileService {
    /**
     * 新增分享文件信息
     * @param username 用户名
     * @param code 提取码
     * @param fileId 文件id
     * @param folderId 文件夹id
     * @param day 分享天数
     * @return 分享信息
     */
    SharedFile add(String username, String code, List<Long> fileId,List<Long>folderId,int day);

    /**
     * 查询当前用户下的文件分享信息
     * @param username
     * @return
     */
    List<SharedFile> searchAll(String username);

    /**
     * 根据url查询文件分享信息
     * @param url
     * @return
     */
    SharedFile searchOne(String url);

    /**
     * 批量删除文件分享链接
     * @param username
     * @param url
     * @return
     */
    int delete(String username,List<String> url);

    /**
     * 发送自动删除过期分享文件信息消息
     *
     * @param url 文件的url
     * @param day
     */
    public void sendMessage(String url, int day);

}
