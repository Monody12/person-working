package com.example.netdisk.entity;

import lombok.Data;

/**
 * @author monody
 * @date 2022/5/1 10:08 下午
 */
@Data
public class MessageBody {
    /** 消息内容 */
    private String content;
    /** 广播转发的目标地址（告知 STOMP 代理转发到哪个地方） */
    private String destination;
}
