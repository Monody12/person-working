package com.example.netdisk.entity.po;

import lombok.Data;

/**
 * @author monody
 * @date 2022/5/20 20:28
 */
@Data
public class UserCheck {
    String username;
    String uuid;

    public UserCheck() {
    }

    public UserCheck(String username, String uuid) {
        this.username = username;
        this.uuid = uuid;
    }
}
