package com.example.netdisk.entity.req;

import lombok.Data;

import java.util.List;

/**
 * @author monody
 * @date 2022/5/1 1:21 下午
 */
@Data
public class ManyId {
    String username;
    List<Long> idList;
    String path;
}
