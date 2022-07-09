package com.example.netdisk.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author monody
 * @date 2022/5/1 9:46 下午
 */
@RestController
public class OnlineEditorController {
    @GetMapping("/broadcast")
    public void broadcast(){
        MyWebSocket.broadcast();
    }


}
