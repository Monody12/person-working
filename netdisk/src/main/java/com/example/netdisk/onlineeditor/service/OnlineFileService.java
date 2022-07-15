package com.example.netdisk.onlineeditor.service;

import com.example.netdisk.entity.po.FileInfo;

/**
 * @ClassName OnlineEditorService
 * @Description TODO
 * @Author monody
 * @Date 2022/7/12 4:31 PM
 * Version 1.0
 */
public interface OnlineFileService {

    /**
     * 获取文件的字符串内容
     * @param fileId
     * @return
     */
    String getFileContent(String fileId);


    String editFile(String fileId);

    /**
     * 将上传文件转为可编辑文件
     * @param fileInfo
     */
    void convertToEdit(FileInfo fileInfo);

    /**
     * 根据当前时间日期生成一个路径（以 / 分隔）
     * 该路径位于编辑文件目录下
     * 该路径对应的文件夹也会被创建（创建时若是Windows系统，则需要为 \ 符号）
     * @return
     */
    String createEditDirAbsolutePath();


}
