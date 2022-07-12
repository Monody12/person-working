package com.example.netdisk.onlinedoc.controller;

import com.example.netdisk.entity.response.BaseResponse;
import com.example.netdisk.entity.response.BaseResponseEntity;
import com.example.netdisk.onlinedoc.entity.OnlineDoc;
import com.example.netdisk.onlinedoc.service.OnlineDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author monody
 * @date 2022/5/1 9:46 下午
 */
@RestController
@RequestMapping("/onlinedoc")
public class OnlineDocController {
    @GetMapping("/broadcast")
    public void broadcast() {
        MyWebSocket.broadcast();
    }

    @Autowired
    OnlineDocService onlineDocService;

    /**
     * 新建在线文档
     */
    @PostMapping("/new")
    public BaseResponseEntity newOnlineDoc(String username, String title) {
        OnlineDoc onlineDoc = onlineDocService.createOnlineDoc(username, title);
        Map<String, Object> map = new HashMap<>(1);
        map.put("onlinedoc", onlineDoc);
        return BaseResponse.success(map);
    }

    /**
     * 根据id删除
     */
    @PostMapping("/delete")
    public BaseResponseEntity deleteOnlineDoc(String username, String id) {
        int i = onlineDocService.deleteOnlineDoc(username, id);
        if (i == 1)
            return BaseResponse.success();
        return BaseResponse.fail();
    }

    /**
     * 根据id更新在线文档标题
     */
    @PostMapping("/updateTitle")
    public BaseResponseEntity updateOnlineDocTitle(String username, String id, String title) {
        int i = onlineDocService.updateOnlineDocTitle( id, title);
        if (i == 1)
            return BaseResponse.success();
        return BaseResponse.fail();
    }

    /**
     * 根据id更新在线文档内容
     */
    @PostMapping("/updateContent")
    public BaseResponseEntity updateOnlineDocContent(String username, String id, String content) {
        int i = onlineDocService.updateOnlineDocContent(id, content);
        if (i == 1)
            return BaseResponse.success();
        return BaseResponse.fail();
    }

    /**
     * 根据id查询在线文档
     */
    @GetMapping("/get")
    public BaseResponseEntity getOnlineDoc(String username, String id) {
        OnlineDoc onlineDoc = onlineDocService.getOnlineDoc(username, id);
        Map<String, Object> map = new HashMap<>(1);
        map.put("onlineDoc", onlineDoc);
        return BaseResponse.success(map);
    }

    /**
     * 查询用户的在线文档摘要（不含内容）
     */
    @GetMapping("/get/list")
    public BaseResponseEntity getOnlineDocsList(String username) {
        List<OnlineDoc> onlineDocs = onlineDocService.getOnlineDocsList(username);
        Map<String, Object> map = new HashMap<>(1);
        map.put("onlinedocs", onlineDocs);
        return BaseResponse.success(map);
    }

}
