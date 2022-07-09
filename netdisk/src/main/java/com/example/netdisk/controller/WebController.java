package com.example.netdisk.controller;

import com.example.netdisk.entity.File;
import com.example.netdisk.entity.Folder;
import com.example.netdisk.entity.vo.PagePath;
import com.example.netdisk.service.FileService;
import com.example.netdisk.service.FolderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author monody
 * @date 2022/5/15 13:50
 */
@Controller
@Slf4j
public class WebController {
    @Autowired
    FileService fileService;
    @Autowired
    FolderService folderService;
    @Autowired
    Base64 base64;

    @GetMapping("/nologin")
    public ModelAndView noLogin(@ApiIgnore HttpServletResponse response){
        ModelAndView mvc = new ModelAndView("forward:/error.jsp");
        mvc.addObject("code",401);
        response.setStatus(401);
        mvc.addObject("info","未登录或登录状态过期");
        // TODO 打印登录页面网址
        mvc.addObject("solve","访问登录页面，重新登录");
        return mvc;
    }

    @GetMapping(value = "index.jsp")
    public ModelAndView index() {
        ModelAndView mvc = new ModelAndView("/error.jsp");
        mvc.addObject("info", "请不要直接访问index.jsp");
        mvc.addObject("solve", "请访问index并提交username和folderPath参数");
        return mvc;
    }

    @GetMapping(value = {"index", ""})
    public ModelAndView file(PagePath pagePath, @ApiIgnore HttpServletRequest request,@ApiIgnore HttpServletResponse response) {
        // 测试能否获取到cookie
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("check".equals(cookie.getName())){
                log.debug("获取到UserChecker: {}",new String(base64.decode(cookie.getValue().getBytes(StandardCharsets.UTF_8))));
                continue;
            }
            log.debug("获取到cookie: {}, value: {}",cookie.getName(),cookie.getValue());
        }

        ModelAndView mvc = new ModelAndView();
        // 没有参数
        if (pagePath.getUsername() == null || pagePath.getFolderPath() == null) {
            mvc.setViewName("error.jsp");
            mvc.addObject("code",404);
            response.setStatus(404);
            mvc.addObject("info", "未找到用户名或文件路径");
            mvc.addObject("solve", "提交用户名username和文件存在位置folderPath参数");
            return mvc;
        }
        // 查询文件和文件夹信息
        PageInfo<Folder> folderPageInfo = folderService.queryByPath(pagePath.getUsername(), pagePath.getFolderPath(), 0, 1000);
        List<File> files = fileService.queryLikePath(pagePath.getUsername(), pagePath.getFolderPath());
        // 识别用户是电脑用户还是移动用户
        String ua = request.getHeader("User-Agent");
        if (ua == null || !ua.contains("Mobile")) {
            pagePath.setDevice("desktop");
        }else{
            pagePath.setDevice("mobile");
        }
        // 封装参数
        mvc.setViewName("index.jsp");
        mvc.addObject("page_path", pagePath);
        mvc.addObject("file_list", files);
        mvc.addObject("folder_list", folderPageInfo.getList());
        return mvc;
    }
}
