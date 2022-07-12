package com.example.netdisk.onlinedoc.service;

import com.example.netdisk.onlinedoc.entity.OnlineDoc;

import java.util.List;

/**
 * @author monody
 * @date 2022/7/11 12:53 下午
 */
public interface OnlineDocService {

    /**
     * 新建在线文档
     * @param username
     * @param title
     * @return
     */
    OnlineDoc createOnlineDoc(String username,String title);

    /**
     * 根据id删除
     * @param username
     * @param id
     * @return
     */
    int deleteOnlineDoc(String username,String id);

    /**
     * 根据id更新在线文档标题
     * @param id
     * @param title
     * @return
     */
    int updateOnlineDocTitle(String id,String title);

    /**
     * 根据id更新在线文档内容
     * @param id
     * @param content
     * @return
     */
    int updateOnlineDocContent(String id,String content);

    /**
     * 根据id查询在线文档
     * @param username
     * @param id
     * @return
     */
    OnlineDoc getOnlineDoc(String username,String id);

    /**
     * 查询用户的在线文档摘要（不含内容）
     * @param username
     * @return
     */
    List<OnlineDoc> getOnlineDocsList(String username);

}
