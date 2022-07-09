package com.example.netdisk.entity.vo;

import lombok.Data;

/**
 * 用于处在的文件夹页
 * @author monody
 * @date 2022/5/15 15:01
 */
@Data
public class PagePath {
    String username;
    String folderPath;
    /**
     * 标识用户的设备
     * desktop | mobile
     */
    String device;
}
