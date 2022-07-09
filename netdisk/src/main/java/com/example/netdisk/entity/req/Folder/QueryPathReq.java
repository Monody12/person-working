package com.example.netdisk.entity.req.Folder;

import lombok.Data;

/**
 * @author monody
 * @date 2022/5/1 2:08 下午
 */
@Data
public class QueryPathReq {
    String username;
    String path;
    String name;
    Integer page;
    Integer size;
}
