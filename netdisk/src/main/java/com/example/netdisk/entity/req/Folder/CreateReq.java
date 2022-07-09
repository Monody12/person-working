package com.example.netdisk.entity.req.Folder;

import lombok.Data;

/**
 * @author monody
 * @date 2022/5/1 2:05 下午
 */
@Data
public class CreateReq {
    String username;
    String name;
    String path;
    String detail;
}
